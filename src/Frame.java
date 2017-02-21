

import java.io.Serializable;
import java.util.Random;


public class Frame implements Serializable{
	
	private static final long serialVersionUID = 1L;
//	private static final int PAYLOAD_LENGTH = 8;	// 8 bytes of input data per frame
//	private static final int GENERATOR = 47933;		// 0xBB3D
//	private static final int GENERATOR_LENGTH = 5;
	
	// Frame Contents
	//private byte sequenceNo;			// Sequence number: 1 byte [0,255]
	//private byte[] data;				// 8 bytes of input data
	
	
	private short sequenceNumber;
	private short payloadLength;
//	private long data;
	private byte[] data;
	private short remainder;

	
	public Frame(short sequenceNumber, short payloadLength, byte[] data) {
		// Read from text file containing data
		this.sequenceNumber = sequenceNumber;
		this.payloadLength = payloadLength;
		this.data = data;
		this.remainder = CRC.performCRC(this.sequenceNumber, this.payloadLength, this.data);
//		if(payloadLength > 1) {
//			gremlin();
//		}
	}
	

	
	public boolean checkCRC() {
		// Hopefully only corrupt packets on server side?
		gremlin();
		return CRC.checkCRC(this.getFullBinary());
	}
	
	public String getSequenceNumberBinary() {
		return CRC.shortToBinary(sequenceNumber);
	}
	
	public String getPayloadLengthBinary() {
		return CRC.shortToBinary(payloadLength);
	}
	
	
	public String getDataBinary() {
		return CRC.byteArrayToBinary(data);
	}
	
	public String getRemainderBinary() {
		return CRC.shortToBinary(remainder);
	}
	
	/**
	 * @return Sequence Number + PayloadLength  + Data in binary
	 */
	public String getOriginalBinary() {
		return getSequenceNumberBinary() + getPayloadLengthBinary() + getDataBinary();
	}
	
	/**
	 * @return Sequence Number + PayloadLength  + Data + Remainder in binary
	 */
	public String getFullBinary() {
		return getSequenceNumberBinary() + getPayloadLengthBinary() + getDataBinary() + getRemainderBinary();
	}
	
	/**
	 * Sets the remainder to specified value. Used by gremlin function only
	 * @param s remainder to be set
	 */
	public void setRemainder(short s){
		this.remainder = s;
	}
	
	
	
	
	
	/*
	 * Normal Getter and Setters
	 */
	public short getSequenceNumber() {
		return sequenceNumber;
	}
	
	public short getPayloadLength() {
		return payloadLength;
	}
	
	public String getData(){
		String string = "";
		for(int i=0;i<data.length;i++){
			string += (char)data[i];
		}
		return string;
	}
	
	public byte[] getDataBytes(){
		return data;
	}
	
	
	
	private void gremlin(){
		Random r = new Random();
		int random = r.nextInt(10);
		//random = 1;	// bypass 
//		if(getSequenceNumber() == 5){
//			random = 8;
//		} else {
//			random = 1;
//		}
		
		// Half of the time corrupt the frame
		if(random > 5){
			if(random == 7){
				// corrupt payload length
				System.out.println("GREMLIN : Corrupting Payload length on frame " + sequenceNumber);
				payloadLength = (short)r.nextInt(65535);
			} 
			else if(random == 6){
				// corrupt sequence number
				System.out.println("GREMLIN : Corrupting Sequence Number on frame " + sequenceNumber);
				sequenceNumber  = (short)r.nextInt(65535);
			} 

			else if(random == 8){
				System.out.println("GREMLIN : Corrupting Data on frame " + sequenceNumber);
				// always corrupt first byte with star
				data[0] = '*';
				
//				randomly corrupt rest of bytes
				for(int i=1;i<data.length;i++){
					if(r.nextInt(2) == 1){
						data[i] = (byte)r.nextInt(256);
					}
				}
			}
			else if(random == 9){
				System.out.println("GREMLIN : Corrupting Remainder on frame " + sequenceNumber);
				remainder = (short)r.nextInt(65535);
			}
		}
	}
	
}
