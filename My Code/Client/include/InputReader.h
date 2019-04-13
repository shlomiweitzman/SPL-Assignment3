
#ifndef BOOST_ECHO_CLIENT_INPUTREADER_H
#define BOOST_ECHO_CLIENT_INPUTREADER_H
#include <string>
#include <boost/asio.hpp>
#include "connectionHandler.h"
#include "EncoderDecoder.h"


using namespace  std;

class InputReader{
private:
    EncoderDecoder encdec;
    ConnectionHandler & connectionHandler;
    string interrupted;
public:
    InputReader(ConnectionHandler &_connectionHandler);
    void process ();
    void interrupt(string msg);
};

#endif //BOOST_ECHO_CLIENT_INPUTREADER_H
