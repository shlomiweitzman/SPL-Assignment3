//
// Created by ellaav@wincs.cs.bgu.ac.il on 12/28/18.
//

#include <InputReader.h>

#include "InputReader.h"

using namespace std;

void InputReader::process() {
    while (interrupted== "false") {
        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
        string line(buf);
        string msg = encdec.encodeMsgFromKB(line);
        if (!connectionHandler.sendBytes(msg.c_str(),msg.length())) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            return;
        }
        while (line=="LOGOUT")
        {
            if(interrupted=="error"){
                interrupted="false";
                break;
            }
            if(interrupted=="ack"){
                break;
            }
        }
    }

}

InputReader::InputReader(ConnectionHandler &_connectionHandler):encdec( EncoderDecoder()),connectionHandler(_connectionHandler),interrupted(
        "false") {

}

void InputReader::interrupt(string msg) {
    interrupted=msg;

}



