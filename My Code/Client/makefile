CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: EchoClient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/EncoderDecoder.o bin/InputReader.o $(LDFLAGS)

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/EncoderDecoder.o bin/InputReader.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

bin/InputReader.o: src/InputReader.cpp
	g++ $(CFLAGS) -o bin/InputReader.o src/InputReader.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
