#include <stdlib.h>
#include <connectionHandler.h>
#include <EncoderDecoder.h>
#include <InputReader.h>
#include <thread>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main(int argc, char *argv[]) {
    EncoderDecoder encdec =  EncoderDecoder();
      if (argc < 3) {
          std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
          return -1;
      }
      std::string host = argv[1];
      short port = atoi(argv[2]);


    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    InputReader inputReader(connectionHandler);

    std::thread th1(&InputReader::process, &inputReader);

    while (1) {




        char opcode[2];


        connectionHandler.getBytes(opcode, 2);
        if (encdec.bytesToShort(opcode) == (short) (10)) {
            char opcodeMsg[2];
            string output = "";
            connectionHandler.getBytes(opcodeMsg, 2);
            string opCMsg = to_string(encdec.bytesToShort(opcodeMsg));
            if (opCMsg == "4" || opCMsg == "7") {
                string output = "";
                string decodedMsg = "";
                char numOfUsers[2];
                connectionHandler.getBytes(numOfUsers, 2);
                int numOfU = encdec.bytesToShort(numOfUsers);
                output.append(to_string(numOfU));
                for (int i = 0; i < numOfU; i++) {
                    decodedMsg="";
                    connectionHandler.getFrameAscii(decodedMsg, '\0');
                    output.append(" " + decodedMsg);

                }
                output = output.substr(0, output.size() - 1);
                cout << "ACK " + opCMsg + " " + output << endl;

            } else if (opCMsg == "8") {
                char decodedMsg[6];
                for (int i = 0; i < 3; i++) {
                    connectionHandler.getBytes(decodedMsg, 2);
                    short out = encdec.bytesToShort(decodedMsg);
                    output.append(to_string(out) + " ");
                }

                cout << "ACK " + opCMsg + " " + output << endl;
            } else if (opCMsg == "3") {
                cout << "ACK " + opCMsg + " " + output << endl;
                inputReader.interrupt("ack");
                break;
            } else {
                cout << "ACK " + opCMsg << endl;
            }
        }
        if (encdec.bytesToShort(opcode) == (short) (9)) {
            string output = "";
            string decodedMsg = "";
            char follow[1];
            connectionHandler.getBytes(follow, 1);
            if (follow[0] == '\0') {
                output.append("NOTIFICATIONS PM ");
            } else output.append("NOTIFICATIONS Public ");
            connectionHandler.getFrameAscii(decodedMsg, '\0');
            decodedMsg = decodedMsg.substr(0, decodedMsg.size() - 1);
            output.append(decodedMsg + " ");
            decodedMsg = "";
            connectionHandler.getFrameAscii(decodedMsg, '\0');
            output.append(decodedMsg);
            output = output.substr(0, output.size() - 1);

            cout << output << endl;
        }
        if (encdec.bytesToShort(opcode) == (short) (11)) {
            char opcodeMsg[2];
            connectionHandler.getBytes(opcodeMsg, 2);
            string opCMsg = to_string((int) encdec.bytesToShort(opcodeMsg));
            if (opCMsg == "3") {
                inputReader.interrupt("error");
            }
            cout << "ERROR " + opCMsg << endl;
        }

    }
    th1.join();

    return 0;
}
