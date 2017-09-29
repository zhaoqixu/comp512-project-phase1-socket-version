// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
//
package ResImpl;

import ResInterface.*;

import java.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

public class ResourceManagerImplThread extends Thread 
{
    Socket socket;
    ResourceManagerImpl rm_server = new ResourceManagerImpl();
    ResourceManagerImplThread (Socket socket, ResourceManagerImpl rm_server)
    {
        this.socket=socket;
        this.rm_server=rm_server;
    }
    // Convert Object to int
    public int gi(Object temp) throws Exception {
        try {
            return (new Integer((String)temp)).intValue();
        }
        catch(Exception e) {
            throw e;
        }
    }
    // Convert Object to boolean
    public boolean gb(Object temp) throws Exception {
        try {
            return (new Boolean((String)temp)).booleanValue();
            }
        catch(Exception e) {
            throw e;
            }
    }
    // Convert Object to String
    public String gs(Object temp) throws Exception {
    try {    
        return (String)temp;
        }
    catch (Exception e) {
        throw e;
        }
    }

    public void run()
    {

    try
        {
        ObjectInputStream inFromClient= new ObjectInputStream(socket.getInputStream());
        PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
        Vector message = null;
        while ((message = (Vector) inFromClient.readObject())!=null){  
            boolean result = false;
            int num;
            Object[] o = new Object[message.size()];
            for (int i = 0 ; i < message.size(); i++) {
                o[i] = message.elementAt(i);
            }
            switch(gs(o[0]).toLowerCase())
            {
                case "newflight": //2
                    if (rm_server.addFlight(gi(o[1]), gi(o[2]), gi(o[3]), gi(o[4])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newcar": //3
                    if (rm_server.addCars(gi(o[1]),gs(o[2]), gi(o[3]), gi(o[4])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newroom": //4
                    if (rm_server.addRooms(gi(o[1]),gs(o[2]),gi(o[3]),gi(o[4])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newcustomer": //5
                    outToClient.println(rm_server.newCustomer(gi(o[1])));
                    break;
                case "deleteflight": //6
                    if (rm_server.deleteFlight(gi(o[1]),gi(o[2])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "deletecar": //7
                    if (rm_server.deleteCars(gi(o[1]),gs(o[2])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "deleteroom": //8
                    if (rm_server.deleteRooms(gi(o[1]),gs(o[2])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "deletecustomer": //9
                    if (rm_server.deleteCustomer(gi(o[1]),gi(o[2])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "queryflight": //10
                    outToClient.println(rm_server.queryFlight(gi(o[1]),gi(o[2])));
                    break;
                case "querycar": //11
                    outToClient.println(rm_server.queryCars(gi(o[1]),gs(o[2])));
                    break;
                case "queryroom": //12
                    outToClient.println(rm_server.queryRooms(gi(o[1]),gs(o[2])));
                    break;
                case "querycustomer": //13
                    outToClient.println(rm_server.queryCustomerInfo(gi(o[1]),gi(o[2])));
                    break;
                case "queryflightprice": //14
                    outToClient.println(rm_server.queryFlightPrice(gi(o[1]),gi(o[2])));
                    break;
                case "querycarprice": //15
                    outToClient.println(rm_server.queryCarsPrice(gi(o[1]),gs(o[2])));
                    break;
                case "queryroomprice": //16
                    outToClient.println(rm_server.queryRoomsPrice(gi(o[1]),gs(o[2])));
                    break;
                case "reserveflight": //17
                    if (rm_server.reserveFlight(gi(o[1]),gi(o[2]),gi(o[3])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "reservecar": //18
                    if (rm_server.reserveCar(gi(o[1]),gi(o[2]),gs(o[3])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "reserveroom": //19
                    if (rm_server.reserveRoom(gi(o[1]),gi(o[2]),gs(o[3])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "itinerary": //20
                    Vector flightNumbers = new Vector();
                    int n = o.length;
                    for(int i=0;i<n-6;i++)
                        flightNumbers.addElement(o[3+i]);
                    if (rm_server.itinerary(gi(o[1]),gi(o[2]), flightNumbers, gs(o[n-3]),gb(o[n-2]),gb(o[n-1])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newcustomerid": //22
                    if (rm_server.newCustomer(gi(o[1]),gi(o[2])))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
            }


        }
        socket.close();
        }
        catch (Exception e)
        {
            if (e instanceof IOException) {
                System.err.println("One of the clients is disconnected!");
            }
        }
    }
}
