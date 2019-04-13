//
// Created by ellaav@wincs.cs.bgu.ac.il on 12/28/18.
//

#include <EncoderDecoder.h>
#include <sstream>

#include "EncoderDecoder.h"


string EncoderDecoder::encodeMsgFromKB(string line) {
     input=split(line,' ');
    string encodedMsg="";
    if(input[0]=="REGISTER"){
        return encod12(1);
    }
    if(input[0]=="LOGIN"){
        return encod12(2);
    }
    if(input[0]=="LOGOUT"){
        return opCToString(3);
    }

    if(input[0]=="FOLLOW"){
        string usersToFollow="";
        for(int i=3;i<(int)input.size();i++){
            usersToFollow.append(input[i]);
                usersToFollow.push_back('\0');
        }
        encodedMsg.append(opCToString(4));
        if(input[1]=="0"){
            encodedMsg.push_back('\0');
        }
        else
            encodedMsg.push_back('\1');

        encodedMsg.append(opCToString(stoi(input[2])));
        encodedMsg.append(usersToFollow);
        return encodedMsg;
    }

    if(input[0]=="POST"){
        encodedMsg.append(opCToString(5));
        encodedMsg.append(line.substr(5,line.size()));
        encodedMsg.push_back('\0');

        return encodedMsg;
    }

    if(input[0]=="PM"){
        encodedMsg.append(opCToString(6));
        string PM="";
        for(int i=2;i<(int)input.size();i++)
            PM.append(input[i]+" ");
        encodedMsg.append(input[1]);
        encodedMsg.push_back('\0');
        encodedMsg.append(PM);
        encodedMsg.push_back('\0');
        return encodedMsg;
    }
    if(input[0]=="USERLIST") {
        return opCToString(7);
    }
    if(input[0]=="STAT"){
        encodedMsg.append(opCToString(8));
        encodedMsg.append(input[1]);
        encodedMsg.push_back('\0');
        return encodedMsg;
    }
    return "";
}


vector <string> EncoderDecoder::split(string &s, char delimiter) {
    vector<string> tokens;
    string tok;
    istringstream tokenStream(s);
    while(getline(tokenStream,tok,delimiter))
        tokens.push_back(tok);
    return tokens;
}

void EncoderDecoder::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

short EncoderDecoder::bytesToShort( char *bytesArr) {

    if(bytesArr== nullptr){
        return short(0);
    }
        short result = (short)((bytesArr[0] & 0xff) << 8);
        result += (short)(bytesArr[1] & 0xff);
        return result;

}

string EncoderDecoder::encod12(int opC) {
    string encodedMsg= opCToString(opC);
    encodedMsg.append(input[1]);
    encodedMsg.push_back('\0');
    encodedMsg.append(input[2]);
    encodedMsg.push_back('\0');
    return encodedMsg;
}

string EncoderDecoder::opCToString(int opC) {
    string encodedMsg="";
    char opcode[2];
    shortToBytes((short)(opC),opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    return encodedMsg;
}

EncoderDecoder::EncoderDecoder():input(vector<string>()) {

}






