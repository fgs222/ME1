package com.example.app_cliente;

public class Buffer {
    private Number[] Buffer;

    public Number[] getBuffer() { return Buffer; }
    public Number getBufferValue(int position) { return Buffer[position]; }

    public void SetBuffer( int bufferSize ) { this.Buffer = new Number[bufferSize]; }
    public void SetBufferValue( int position, Number value ){ this.Buffer[position] = value; }
}
