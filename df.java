import java.io.*;
//Main class for getting the data from user and performing action
class df{
  //Main function
  public static void main(String[] args) {
  performAction performaction = new performAction();
  String inputFile = args[0];
  //performaction.callme(inputFile);
  performaction.readFile(inputFile);
  /*for (int i =0;i<=args.length ;i++ ) {
    System.out.println(args[i]);
  }
  */
  }
}
// class for partition information
class partition{
  int size;
  int type;
  int startSector;
}

//class that performs action
class performAction{
  private static final int BUFFER_SIZE = 16;
  int offset = 16;
  int noEntry = 0;
  String volumeType ;
  int[] partitionTable = new int[64];

  public String checkFileSystemType(int type){
    switch (type){
      case 0: return "Unknown or empty";
      case 1: return "12-bit FAT";
      case 4: return "16-bit FAT (< 32MB)";
      case 5: return "Extended MS-DOS Partition";
      case 6: return "FAT-16 (32MB to 2GB)";
      case 7: return "NTFS";
      case 11: return "FAT-32";
      case 14: return "FAT-16 (LBA)";
      default:
        noEntry = noEntry+1;
        return "NOT-DECODED";

    }
  }


  //another way of doing the same thing
  public void readFile(String inputFile){
    partition[] part = new partition[4];

    try {

          int ch;
           // create a new RandomAccessFile with filename test
           RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
           // set the file pointer at 0 position
           for (int i=0;i<=63 ;i++ ) {
             raf.seek(0x1BE+i);
             partitionTable[i] = raf.read();
             //System.out.print(partitionTable[i]);
           }

           for (int i = 0;i<4 ;i++ ) {
             String[] startSectorArray = new String[4];
             String[] sizeArray = new String[4];
             String startSector = "";
             String size = "";
             String fileSystemType = "";
             part[i] = new partition();
             part[i].type = partitionTable[0x04+(i*offset)];
             //To track number of valid partition
             if(part[i].type == 0){
               noEntry = noEntry+1;
             }
             fileSystemType = checkFileSystemType(part[i].type);
             //Code to get the startSector in hex
             for (int j = 0;j<4 ;j++ ) {
               int count = 3 - j;
               String hex = Integer.toHexString(partitionTable[0x08+(i*offset)+j]);
               String hexSize = Integer.toHexString(partitionTable[0x0C+(i*offset)+j]);
               //Now we must change the number to big-endian
               startSectorArray[count] = hex;
               sizeArray[count] = hexSize;
               //System.out.print("Value of count = "+count+"value stored in array = "+startSectorArray[count]);
             }
             //code to create the startSector from startSectorArray
             for (int k = 0;k<4 ;k++ ) {
                //System.out.print(startSectorArray[k]);
               startSector = startSector+startSectorArray[k];
               size = size+sizeArray[k];
             }
             //System.out.print(startSector);
             part[i].startSector = Integer.parseInt(startSector, 16);
             part[i].size = Integer.parseInt(size, 16);
             System.out.println("The Partition "+ i + " : " + "Type = " + fileSystemType+ " Start Sector is " + part[i].startSector + " Size is = " + part[i].size);
           }

        } catch (IOException ex) {
           ex.printStackTrace();
        }

        System.out.print("Total number of valid partition is = "+ (4-noEntry));

  }

}
