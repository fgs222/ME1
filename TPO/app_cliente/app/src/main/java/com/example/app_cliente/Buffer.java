package com.example.app_cliente;

public class Buffer {
    private String[] Buffer;
    //private float[] Buffer;

    public String[] getBuffer() { return Buffer; }
    public String getBufferValue(int position) { return Buffer[position]; }
    public int getBufferValueAsInt(int position) { return Integer.parseInt( Buffer[position] ); }

    public void SetBuffer( int bufferSize ) { this.Buffer = new String[bufferSize]; }
    public void SetBufferValue( int position, String value ){ this.Buffer[position] = value; }
}
