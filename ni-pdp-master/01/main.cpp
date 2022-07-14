#include <iostream>
#include <sstream>
#include <string>
#include <list>
#include <vector>
#include <stdexcept>
#include <unistd.h>
#include <fstream>

using namespace std;

short BOARD_SIZE = 0;
short MAX_DEPTH = 0;
short SOLDIERS_CNT = 0;
enum TURN {BISHOP, KNIGHT};

struct BoardMove {
    int to = -1;
    bool soldierTaken = false;

    explicit BoardMove(const int &to) : to(to) {}
};

class BoardState {
public:
    vector<char> boardState;
    TURN playerOnTurn;
    short bishopPosition;
    short knightPosition;

    explicit BoardState(vector<char> boardState, const short &bishopPosition, const short &knightPosition)
    : boardState(std::move(boardState)), bishopPosition(bishopPosition), knightPosition(knightPosition) {
        playerOnTurn = BISHOP;
    }

    BoardState(BoardState &other) = default;

    list<unsigned short> next();
    short val(const short &index);
    void switchPlayers();
    bool makeMove(const short &to); // return true if soldier was captured

private:
    bool isSoldierOnOtherDiagonal(const short &position);
    list<unsigned short> getPossibleMovesForBishop();
    list<unsigned short> getPossibleMovesForKnight();
};
list<unsigned short> BoardState::next() {
    if (playerOnTurn == KNIGHT) {
        return getPossibleMovesForKnight();
    }
    return getPossibleMovesForBishop();
}

short BoardState::val(const short &index) {
    if (index >= BOARD_SIZE * BOARD_SIZE || index < 0
        || boardState.at(index) == 'S' || boardState.at(index) == 'J') {
        return -1;
    }
    if (boardState.at(index) == 'P') {
        return 2;
    }
    if (playerOnTurn == BISHOP && isSoldierOnOtherDiagonal(index)) {
        return 1;
    }
    return 0;
}

void BoardState::switchPlayers() {
    if (playerOnTurn == BISHOP) {
        playerOnTurn = KNIGHT;
    } else {
        playerOnTurn = BISHOP;
    }
}

bool BoardState::makeMove(const short &to) {
    const short from = playerOnTurn == BISHOP ? bishopPosition : knightPosition;
    boardState.at(from) = '-';

    bool soldierTaken = false;
    if (boardState.at(to) == 'P') {
        soldierTaken = true;
    } else if (boardState.at(to) != '-') {
        throw std::runtime_error("Error in Solution::makeMove. Unexpected code: " + to_string(boardState.at(to)));
    }

    if (playerOnTurn == BISHOP) {
        boardState.at(to) = 'S';
        bishopPosition = to;
    } else {
        boardState.at(to) = 'J';
        knightPosition = to;
    }

    switchPlayers();
    return soldierTaken;
}

bool BoardState::isSoldierOnOtherDiagonal(const short &position) {
    for (unsigned short i = 1; i < BOARD_SIZE; i++) {
        if (position - i * BOARD_SIZE >= 0) {
            if ((position%BOARD_SIZE + i < BOARD_SIZE && boardState.at(position - i * BOARD_SIZE + i) == 'P')
                || (position%BOARD_SIZE - i >= 0 && boardState.at(position - i * BOARD_SIZE - i) == 'P')) {
                return true;
            }
        }
        if (position + i * BOARD_SIZE < BOARD_SIZE * BOARD_SIZE) {
            if ((position%BOARD_SIZE + i < BOARD_SIZE && boardState.at(position + i * BOARD_SIZE + i) == 'P')
                || (position%BOARD_SIZE - i >= 0 && boardState.at(position + i * BOARD_SIZE - i) == 'P')) {
                return true;
            }
        }
    }
    return false;
}

list<unsigned short> BoardState::getPossibleMovesForBishop() {
    list<unsigned short> possibleMoves; // value 2 in front, value 2 in back
    list<unsigned short> worstMoves; // where val == 0

    bool rightUpBlocked = false, rightDownBlocked = false, leftUpBlocked = false, leftDownBlocked = false;
    for (short i = 1 ; i < BOARD_SIZE ; i++) {
        if (rightUpBlocked && rightDownBlocked && leftUpBlocked && leftDownBlocked) {
            break;
        }
        // right up
        const short rightUpIndex = (bishopPosition - i * BOARD_SIZE >= 0 && bishopPosition%BOARD_SIZE + i < BOARD_SIZE) ? bishopPosition - i * BOARD_SIZE + i : -1;
        const short rightUpVal = val(rightUpIndex);
        if (!rightUpBlocked && rightUpVal > -1) {
            if (rightUpVal == 0) {
                worstMoves.emplace_back(rightUpIndex);
            } else if (rightUpVal == 1) {
                possibleMoves.emplace_back(rightUpIndex);
            } else { // val == 2
                possibleMoves.emplace_front(rightUpIndex);
                rightUpBlocked = true;
            }
        } else {
            rightUpBlocked = true;
        }
        // right down
        const short rightDownIndex = (bishopPosition + i * BOARD_SIZE + i < BOARD_SIZE * BOARD_SIZE && bishopPosition%BOARD_SIZE + i < BOARD_SIZE) ? bishopPosition + i * BOARD_SIZE + i : -1;
        const short rightDownVal = val(rightDownIndex);
        if (!rightDownBlocked && rightDownVal > -1) {
            if (rightDownVal == 0) {
                worstMoves.emplace_back(rightDownIndex);
            } else if (rightDownVal == 1) {
                possibleMoves.emplace_back(rightDownIndex);
            } else { // val == 2
                possibleMoves.emplace_front(rightDownIndex);
                rightDownBlocked = true;
            }
        } else {
            rightDownBlocked = true;
        }
        // left up
        const short leftUpIndex = (bishopPosition - i * BOARD_SIZE - i >= 0 && bishopPosition%BOARD_SIZE - i >= 0) ? bishopPosition - i * BOARD_SIZE - i : -1;
        const short leftUpVal = val(leftUpIndex);
        if (!leftUpBlocked && leftUpVal > -1) {
            if (leftUpVal == 0) {
                worstMoves.emplace_back(leftUpIndex);
            } else if (leftUpVal == 1) {
                possibleMoves.emplace_back(leftUpIndex);
            } else { // val == 2
                possibleMoves.emplace_front(leftUpIndex);
                leftUpBlocked = true;
            }
        } else {
            leftUpBlocked = true;
        }
        // left down
        const short leftDownIndex = (bishopPosition + i * BOARD_SIZE < BOARD_SIZE * BOARD_SIZE && bishopPosition%BOARD_SIZE - i >= 0) ? bishopPosition + i * BOARD_SIZE - i : -1;
        const short leftDownVal = val(leftDownIndex);
        if (!leftDownBlocked && leftDownVal > -1) {
            if (leftDownVal == 0) {
                worstMoves.emplace_back(leftDownIndex);
            } else if (leftDownVal == 1) {
                possibleMoves.emplace_back(leftDownIndex);
            } else { // val == 2
                possibleMoves.emplace_front(leftDownIndex);
                leftDownBlocked = true;
            }
        } else {
            leftDownBlocked = true;
        }
    }

    possibleMoves.splice(possibleMoves.end(), worstMoves);
    if (possibleMoves.empty()) {
        possibleMoves.emplace_back(bishopPosition);
    }
    return possibleMoves;
}

list<unsigned short> BoardState::getPossibleMovesForKnight() {
    list<unsigned short> possibleMoves;

    const int indexes[] = {
            // up ->
            (knightPosition - 2 * BOARD_SIZE >= 0 && knightPosition%BOARD_SIZE + 1 < BOARD_SIZE) ?
                knightPosition - 2 * BOARD_SIZE + 1 : -1,
            // up <-
            (knightPosition - 2 * BOARD_SIZE - 1 >= 0 && knightPosition%BOARD_SIZE - 1 >= 0) ?
                knightPosition - 2 * BOARD_SIZE - 1 : -1,
            // -> up
            (knightPosition - BOARD_SIZE >= 0 && knightPosition%BOARD_SIZE + 2 < BOARD_SIZE) ?
                knightPosition - BOARD_SIZE + 2 : -1,
            // <- up
            (knightPosition - BOARD_SIZE - 2 >= 0 && knightPosition%BOARD_SIZE - 2 >= 0) ?
                knightPosition - BOARD_SIZE - 2 : -1,
            // down ->
            (knightPosition + 2 * BOARD_SIZE + 1 < BOARD_SIZE * BOARD_SIZE && knightPosition%BOARD_SIZE + 1 < BOARD_SIZE) ?
                knightPosition + 2 * BOARD_SIZE + 1 : -1,
            // down <-
            (knightPosition + 2 * BOARD_SIZE < BOARD_SIZE * BOARD_SIZE && knightPosition%BOARD_SIZE - 1 >= 0) ?
                knightPosition + 2 * BOARD_SIZE - 1 : -1,
            // -> down
            (knightPosition + BOARD_SIZE + 2 < BOARD_SIZE * BOARD_SIZE && knightPosition%BOARD_SIZE + 2 < BOARD_SIZE) ?
                knightPosition + BOARD_SIZE + 2 : -1,
            // <- down
            (knightPosition + BOARD_SIZE < BOARD_SIZE * BOARD_SIZE && knightPosition%BOARD_SIZE - 2 >= 0) ?
                knightPosition + BOARD_SIZE - 2 : -1
    };

    for (const int &i : indexes) {
        const short value = val((short)i);
        if (value == 0) {
            possibleMoves.emplace_back(i);
        } else if (value == 2) {
            possibleMoves.emplace_front(i);
        }
    }
    if (possibleMoves.empty()) {
        possibleMoves.emplace_back(knightPosition);
    }
    return possibleMoves;
}

long long CALL_CNT = 0;
bool findBestPlay(BoardState &currentBoard, vector<BoardMove> &currentMoves,
                  short &remainingSoldiers, const short currentDepth) {
    CALL_CNT++;
    if (currentDepth + remainingSoldiers >= MAX_DEPTH) {
        return false;
    }

//    if (remainingSoldiers <= 0) {
//        if (currentDepth < MAX_DEPTH) {
//            MAX_DEPTH = currentDepth;
//        }
//        return true;
//    }

    vector<BoardMove> bestMoves;
    list<unsigned short> movesToExplore = currentBoard.next();
    while (!movesToExplore.empty()) {
        const short toPosition = movesToExplore.front();
        movesToExplore.pop_front();

        // prepare variables for neighbour + make move
        BoardState neighbourBoard(currentBoard);
        currentMoves.at(currentDepth).to = toPosition;
        currentMoves.at(currentDepth).soldierTaken = neighbourBoard.makeMove(toPosition);

        // if current move bring solution, end cycle
        short neighbourRemainingSoldiers = remainingSoldiers;
        if (currentMoves.at(currentDepth).soldierTaken) {
            if(--neighbourRemainingSoldiers == 0) {
                if (currentDepth + 1 < MAX_DEPTH) {
                    MAX_DEPTH = currentDepth + 1;
                }
                return true;
            }
        }

        if (findBestPlay(neighbourBoard,currentMoves,neighbourRemainingSoldiers,currentDepth + 1)) {
            bestMoves = currentMoves;

            // if optimum has been found
            if (currentMoves.at(currentDepth + remainingSoldiers).to == -1) {
                return true;
            }
        }
        currentMoves.at(currentDepth).to = -1;
    }

    if (!bestMoves.empty()) {
        currentMoves = bestMoves;
    }
    return !bestMoves.empty();
}

void printBoardState(const vector<char> &boardState) {
    int i = 0;
    for (const char &c : boardState) {
        cout << c;
        if (++i == BOARD_SIZE) {
            cout << endl;
            i = 0;
        }
    }
}

/**
 * Loads specified file
 * Based on CLion behavior -> gets parent of folder in which this program is running
 *                            and then assumes that the file is in testData folder
 */
BoardState* loadInstance(const string &fileName) {
    char buff[FILENAME_MAX]; //create string buffer to hold path
    getcwd(buff, FILENAME_MAX);
    string current_working_dir(buff);
    string path = string(current_working_dir + "/" + fileName);
    ifstream infile(path);
    string line;
    // first load board dimension + upper bound
    getline(infile, line);
    istringstream iss1(line);
    iss1 >> BOARD_SIZE;

    getline(infile, line);
    istringstream iss2(line);
    iss2 >> MAX_DEPTH;

    // load board
    vector<char> boardState;
    short bishopPosition = -1;
    short knightPosition = -1;
    while (getline(infile, line)) {
        istringstream issTmp(line);
        string lineText;
        if (!(issTmp >> lineText)) {
            break;
        }
        for (char const &c: lineText) {
            boardState.emplace_back(c);
            if (c == 'P') {
                SOLDIERS_CNT++;
            }
            if (c == 'S') {
                bishopPosition = boardState.size() - 1;
            }
            if (c == 'J') {
                knightPosition = boardState.size() - 1;
            }
        }
    }

    if (bishopPosition == -1 || knightPosition == -1) {
        throw std::runtime_error("Error in loadInstance. Bishop or knight not found.");
    }

    return new BoardState(boardState, bishopPosition, knightPosition);
}

void printFoundMovesSequence(const vector<BoardMove> &moves) {
    int i = 0;
    cout << "Detail of moves:" << endl;
    for (BoardMove move : moves) {
        cout << (i++%2 == 0 ? "S to " : "J to ") << to_string(move.to) << (move.soldierTaken ? "*": "") << endl;
        // [x, y] version
//        cout << (i++%2 == 0 ? "S to " : "J to ");// << to_string(move.to) << (move.soldierTaken ? "*": "") << endl;
//        cout << "[" << to_string(move.to%BOARD_SIZE) << ", " << to_string(move.to/BOARD_SIZE) << "]";
//        cout << (move.soldierTaken ? "*": "") << endl;
    }
}

void countAndPrintCaptures(const vector<BoardMove> &moves) {
    int sum = 0;
    for (BoardMove move : moves) {
        if (move.soldierTaken) {
            sum++;
        }
    }
    cout << "\tCaptured soldiers: " << sum << endl;
}

// compile with -O3 -funroll-loops
int main(int argc, char *argv[]) {
    if (argc != 2) {
        cout << "This program expects 1 argument -> input file name (relative path possible)" << endl;
        return 1;
    }
    const string FILE_NAME = argv[1];

    BoardState* initialBoardState = loadInstance(FILE_NAME);
//    printBoardState(initialBoardState.boardState);

    clock_t startTime = clock();
    vector<BoardMove> moves(MAX_DEPTH, BoardMove(-1));
    const bool solutionFound = findBestPlay(*initialBoardState, moves, SOLDIERS_CNT, 0);
    clock_t endTime = clock();
    while (moves.size() != (unsigned)MAX_DEPTH) {
        moves.pop_back();
    }
    const double measuredTime = double(endTime - startTime) / CLOCKS_PER_SEC;
    /*cout << "File name: " << FILE_NAME << endl;
    cout << "Result: " << (solutionFound ? "SUCCESS" : "FAIL") << endl;
    cout << "\tSoldier count: " << to_string(SOLDIERS_CNT) << endl;
    countAndPrintCaptures(moves);
    cout << "\tMoves cnt: " << moves.size() << endl;
    cout << "\tCall cnt: " << CALL_CNT << endl;
    cout << "\tTime: " << to_string(measuredTime) << " (s)" << endl;*/
    cout << FILE_NAME << "," << to_string(measuredTime) << "," << CALL_CNT << ",1,1,seq" << endl;
//    printFoundMovesSequence(moves);
    return 0;
}