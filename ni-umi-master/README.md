Tento repozitář obsahuje materiály z předmětu **NI-UMI**.

By: @schejond

**01**

Tato složka obsahuje projekt k prvnímu cvičení. Ve složce dataset jsou přiloženy dva testovací vstupy na vyzkoušení funkčnosti programu.
Program stačí stáhnout, otevřít a spustit jako projekt např. v InteliJIdee.
Program řeší první úkol z daného cvičení, tedy problém vysavače. Řešení je provedeno pomocí algoritmu a*.
Algoritmus hledá optimální cestu následujícím způsobem:

*  Ze startu vyhledá pomocí heuristiky cestu k nejbližsímu smetí
*  Dále tuto pozici považuje za "nový start"a opakuje první krok
*  To vše dokud "nevysaje" všechno smetí


**02**

Tato složka obsahuje projekt k druhému cvičení. Jedná se o implementaci úlohy řešiče sudoku.
Implementovány jsou 2 algoritmy: obyčejný backtracking a back jumping.
U algoritmů byla vždy měřena výpočetní složitost, aby mezi sebou šly porovnat (měřil se počet vykonaných iterací algoritmu).

Video ukázka fungování algoritmu je v adresáři.


**04**

Tato složka obsahuje implementaci k čtvrté úloze z 4. cvičení. Obsahuje definovanou doménu a akce pro robota Shakeyho.
Cíl je možné libovolně obměnit. Syntax je v souladu s použitým solverem Fast Downward.
Ten se sprovozní spuštěním souboru build.py v adresáří FastDownward/.
Puštění s mnou definovanou doménou a akcemi pak pomocí příkazu:
./fast-downward.py ../shakey-domain.pddl ../shakey-instl.pddl --search "astar(lmcut())"

V případě problémů s instalací a spuštěním Fast Downwardu jde najít pomoc zde:
http://www.fast-downward.org/ObtainingAndRunningFastDownward

Příklady úspěšně vyřešených problému jsou jako obrázky v adresáři.
Video ukázka taktéž.
