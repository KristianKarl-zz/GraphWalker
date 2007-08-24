#include "redir.h"

#define EXPORTED extern "C" __declspec( dllexport )

int EXPORTED MBT_Initialize( char* mbtJar, char* graphmlFile, int random, int time );

int EXPORTED MBT_GetAction( char* action );

int EXPORTED MBT_Backtrack();

int EXPORTED MBT_Forward();

int EXPORTED MBT_Stop();


CRedirector redir;