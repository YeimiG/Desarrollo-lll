/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;

/**
 *
 * @author owen7
 */
public class NodeData {
   
    private String nodeName;
    private String IPAddress;
    private int socketNum;

    public NodeData(String pnodeName, String pIPAddress, int psocketNum) {
        this.nodeName = pnodeName;
        this.IPAddress = pIPAddress;
        this.socketNum = psocketNum;
    }

    public String getNodeName() { return nodeName; }
    public String getIPAddress() { return IPAddress; }
    public int getSocketNum() { return socketNum; }

}
