(define (domain shakey)
    (:requirements :strips :typing)
    (:types place)
    (:predicates
    	; define place related predicates
		(neighbour ?place1 ?place2)
		(hasShakey ?place)
		(hasSwitchOn ?place)
		(hasSwitchOff ?place)
		(hasBox ?place)
		(noBox ?place)

		; define places
		(A ?place)
		(B ?place)
		(C ?place)
		(D ?place)
		(E ?place)
		(F ?place)
		(G ?place)
		(H ?place)
		(I ?place)
		(J ?place)
		(K ?place)
		(L ?place)
		(M ?place)
		(N ?place)
		(O ?place)
		(P ?place)
		(Q ?place)
		(R ?place)

		; define robot states
		(robotHasEmptyHands)
		(robotHasBox)
		(onGround)
		(onBox)
    )
;-----------------------------------------------------    
;move actions - posuny po grafu
    (:action move
        :parameters (?from - place ?to - place)
        :precondition (and
            (neighbour ?from ?to)
            (hasShakey ?from)
            (onGround)
            (noBox ?to)
        )
        :effect (and
        	(not (hasShakey ?from))
        	(hasShakey ?to)
        )
    )
;-----------------------------------------------------    
;box handling actions
    (:action pickUpBox
        :parameters (?currentPos - place ?boxPos - place)
        :precondition (and
        	(hasShakey ?currentPos)
        	(neighbour ?currentPos ?boxPos)
        	(hasBox ?boxPos)
        	(robotHasEmptyHands)
        )
        :effect (and
        	(not (robotHasEmptyHands))
        	(robotHasBox)
        	(not (hasBox ?boxPos))
        	(noBox ?boxPos)
        )
    )

    (:action putDownBox
        :parameters (?currentPos - place ?to - place)
        :precondition (and
        	(hasShakey ?currentPos)
        	(neighbour ?currentPos ?to)
        	(robotHasBox)
        	(noBox ?to)
        )
        :effect (and
        	(not (robotHasBox))
        	(robotHasEmptyHands)
        	(hasBox ?to)
        	(not (noBox ?to))
        )
    )  
;-----------------------------------------------------    
;box climbing actions 
    (:action climbBox
        :parameters (?currentPos - place ?boxPos - place)
        :precondition (and
            (hasShakey ?currentPos)
            (neighbour ?currentPos ?boxPos)
            (onGround)
            (hasBox ?boxPos)
            (robotHasEmptyHands)
        )
        :effect (and
        	(not (onGround))
        	(onBox)
        	(not (hasShakey ?currentPos))
        	(hasShakey ?boxPos)
        )
    )

    (:action climbDownFromBox
        :parameters (?currentPos - place ?to - place)
        :precondition (and
            (onBox)
            (hasShakey ?currentPos)
            (neighbour ?currentPos ?to)
            (hasBox ?currentPos)
            (noBox ?to)
            (robotHasEmptyHands)
        )
        :effect (and
        	(not (onBox))
        	(onGround)
        	(hasShakey ?to)
        	(not (hasShakey ?currentPos))
        )
    )
;-----------------------------------------------------    
;switch actions
    (:action turnOnSwitch
    	:parameters (?pos - place)
    	:precondition (and 
    		(hasShakey ?pos)
    		(hasSwitchOff ?pos)
    		(onBox)
    		(hasBox ?pos)
            (robotHasEmptyHands)
    	)
    	:effect (and
    		(hasSwitchOn ?pos)
    		(not (hasSwitchOff ?pos))
    	)
    )

    (:action turnOffSwitch
    	:parameters (?pos - place)
    	:precondition (and 
    		(hasShakey ?pos)
    		(hasSwitchOn ?pos)
    		(onBox)
    		(hasBox ?pos)
            (robotHasEmptyHands)
    	)
    	:effect (and
    		(hasSwitchOff ?pos)
    		(not (hasSwitchOn ?pos))
    	)
    )
)