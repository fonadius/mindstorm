
#include "TCPServerSimple.h"
int __cdecl main(void)
{
	TCPServerSimple server;
	server.waitForConnection();
	server.sentData(32);
	server.sentData(10);
	server.sentData(20);
}

