//
// Created by ellaav@wincs.cs.bgu.ac.il on 12/28/18.
//

#ifndef BOOST_ECHO_CLIENT_ENCODERDECODER_H
#define BOOST_ECHO_CLIENT_ENCODERDECODER_H

#include <vector>
#include <string>

using namespace std;

class EncoderDecoder {
private:
    vector<string> input;
public:

    EncoderDecoder();
    string encodeMsgFromKB(string line);
    vector<string> split(string& s,char delimiter);
    void shortToBytes(short num, char* bytesArr);
    short bytesToShort( char* bytesArr);
    string encod12(int opC);
    string opCToString(int opC);
 //   void stringToCharArray(string s, char*out);
};
#endif //BOOST_ECHO_CLIENT_ENCODERDECODER_H
