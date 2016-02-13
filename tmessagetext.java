// Send packet
String messageStr = "https://www.youtube.com/watch?v=enMReCEcHiM"
int server_port = 1898;
DatagramSocket s; 

// Recieve packet 
String text;
int server_port = 1898;
byte[] message = new byte[1500];
DatagramPacket p = DatagramPacket(message, message.length);
DatagramSocket s = new DatagramSocket(server_port);
s.receive(p);
text = new String(message, 0, p.getLength());
Log.d("text goes here", "message:" + text);

