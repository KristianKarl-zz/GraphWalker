// mbt.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "mbt.h"


int MBT_Initialize( char* mbtJar, char* graphmlFile, int random, int time )
{
  CString cmdLine;
  cmdLine.Format( "java -jar %s dynamic -o -g %s", mbtJar, graphmlFile );
  redir.Open( cmdLine );
  return 0;
}



int MBT_GetAction( char* action )
{
  if ( strcpy( action, redir.output() ) == 0 )
  {
    return 1;
  }
  return 0;
}



int MBT_Backtrack()
{
  redir.Printf("1\r\n");
  return 0;
}



int MBT_Forward()
{
  redir.Printf("0\r\n");
  return 0;
}



int MBT_Stop()
{
  redir.Printf("2\r\n");
  return 0;
}


