// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
//
package MidImpl;

import java.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MiddleWareImplThread extends Thread 
{
    Socket socket;
    Socket socket_flight;
    Socket socket_car;
    Socket socket_room;

    MiddleWareImpl rm_server = new MiddleWareImpl();
    MiddleWareImplThread (Socket socket, Socket socket_flight, Socket socket_car, Socket socket_room, MiddleWareImpl rm_server)
    {
        this.socket=socket;
        this.socket_flight=socket_flight;
        this.socket_car=socket_car;
        this.socket_room=socket_room;
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
        //client stream
        ObjectInputStream inFromClient= new ObjectInputStream(socket.getInputStream());
        PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
        //flight stream
        ObjectOutputStream outToFlight = new ObjectOutputStream(socket_flight.getOutputStream());
        BufferedReader inFromFlight = new BufferedReader(new InputStreamReader(socket_flight.getInputStream()));
        //car stream
        ObjectOutputStream outToCar = new ObjectOutputStream(socket_car.getOutputStream());
        BufferedReader inFromCar = new BufferedReader(new InputStreamReader(socket_car.getInputStream()));
        //room stream
        ObjectOutputStream outToRoom = new ObjectOutputStream(socket_room.getOutputStream());
        BufferedReader inFromRoom = new BufferedReader(new InputStreamReader(socket_room.getInputStream()));


        Vector message = null;
        while ((message = (Vector) inFromClient.readObject())!=null){  
            boolean result = false;
            int num;
            Object[] o = new Object[message.size()];
            for (int i = 0 ; i < message.size(); i++) {
                o[i] = message.elementAt(i);
            }
            oloop:
            switch(gs(o[0]).toLowerCase())
            {
                case "newflight": //2
                    outToFlight.writeObject(message);
                    if(inFromFlight.readLine().equals("true"))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newcar": //3
                    outToCar.writeObject(message);
                    if (inFromCar.readLine().equals("true"))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newroom": //4
                    outToRoom.writeObject(message);
                    if (inFromRoom.readLine().equals("true"))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "newcustomer": //5
                    outToClient.println(rm_server.newCustomer(gi(o[1])));
                    break;
                case "deleteflight": //6
                    outToFlight.writeObject(message);
                    if(inFromFlight.readLine().equals("true"))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "deletecar": //7
                    outToCar.writeObject(message);
                    if (inFromCar.readLine().equals("true"))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "deleteroom": //8
                    outToRoom.writeObject(message);
                    if (inFromRoom.readLine().equals("true"))
                        outToClient.println("true");
                    else
                        outToClient.println("false");
                    break;
                case "deletecustomer": //9
                    Trace.info("RM::deleteCustomer(" + gi(o[1]) + ", " + gi(o[2]) + ") called" );
                    Customer cust_d = (Customer) rm_server.readData( gi(o[1]), Customer.getKey(gi(o[2])) );
                    if ( cust_d == null ) {
                        Trace.warn("RM::deleteCustomer(" + gi(o[1]) + ", " + gi(o[2]) + ") failed--customer doesn't exist" );
                        outToClient.println("false");
                    } else {            
                        // Increase the reserved numbers of all reservable items which the customer reserved. 
                        RMHashtable reservationHT = cust_d.getReservations();
                        for (Enumeration e = reservationHT.keys(); e.hasMoreElements();) {        
                            String reservedkey = (String) (e.nextElement());
                            ReservedItem reserveditem = cust_d.getReservedItem(reservedkey);
                            int reservedCount = reserveditem.getCount();

                            switch (reservedkey.charAt(0)) {
                                case 'c':
                                    //rm_car.freeItemRes(id, customerID, reservedkey, reservedCount);
                                    Vector updatecar = new Vector();
                                    updatecar.addElement("updatecar");
                                    updatecar.addElement(gs(o[1]));
                                    updatecar.addElement(gs(o[2]));
                                    updatecar.addElement(reservedkey);
                                    updatecar.addElement(String.valueOf(reservedCount));
                                    outToCar.writeObject(updatecar);
                                    if(inFromCar.readLine().equals("false")){
                                        outToClient.println("false");
                                    }
                                    break;
                                case 'f':
                                    //rm_flight.freeItemRes(id, customerID, reservedkey, reservedCount);
                                    Vector updateflight = new Vector();
                                    updateflight.addElement("updateflight");
                                    updateflight.addElement(gs(o[1]));
                                    updateflight.addElement(gs(o[2]));
                                    updateflight.addElement(reservedkey);
                                    updateflight.addElement(String.valueOf(reservedCount));
                                    outToFlight.writeObject(updateflight);
                                    if(inFromFlight.readLine().equals("false")){
                                        outToClient.println("false");
                                    }
                                    break;
                                case 'r':
                                    //rm_room.freeItemRes(id, customerID, reservedkey, reservedCount);
                                    Vector updateroom = new Vector();
                                    updateroom.addElement("updateroom");
                                    updateroom.addElement(gs(o[1]));
                                    updateroom.addElement(gs(o[2]));
                                    updateroom.addElement(reservedkey);
                                    updateroom.addElement(String.valueOf(reservedCount));
                                    outToRoom.writeObject(updateroom);
                                    if(inFromRoom.readLine().equals("false")){
                                        outToClient.println("false");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        // remove the customer from the storage
                        rm_server.removeData(gi(o[1]), cust_d.getKey());
                                
                        Trace.info("RM::deleteCustomer(" + gi(o[1]) + ", " + gi(o[2]) + ") succeeded" );
                        outToClient.println("true");
                    } // if
                    break;
                case "queryflight": //10
                    outToFlight.writeObject(message);
                    outToClient.println(inFromFlight.readLine());
                    break;
                case "querycar": //11
                    outToCar.writeObject(message);
                    outToClient.println(inFromCar.readLine());
                    break;
                case "queryroom": //12
                    outToRoom.writeObject(message);
                    outToClient.println(inFromRoom.readLine());
                    break;
                case "querycustomer": //13
                    outToClient.println(rm_server.queryCustomerInfo(gi(o[1]),gi(o[2])));
                    break;
                case "queryflightprice": //14
                    outToFlight.writeObject(message);
                    outToClient.println(inFromFlight.readLine());
                    break;
                case "querycarprice": //15
                    outToCar.writeObject(message);
                    outToClient.println(inFromCar.readLine());
                    break;
                case "queryroomprice": //16
                    outToRoom.writeObject(message);
                    outToClient.println(inFromRoom.readLine());                    
                    break;
                case "reserveflight": //17
                    Customer cust = (Customer) rm_server.readData(gi(o[1]), Customer.getKey(gi(o[2])));
                    String key = ("flight-" + gi(o[3])).toLowerCase(); 
                    if ( cust == null ) {
                        Trace.warn("RM::reserveFlight( " + gi(o[1]) + ", " + gi(o[2]) + ", " + key + ", "+ gi(o[3])+")  failed--customer doesn't exist" );
                        outToClient.println("false");
                        break;
                    } else {
                        Vector pricequery = new Vector();
                        pricequery.addElement("queryflightprice");
                        pricequery.addElement(gs(o[1]));
                        pricequery.addElement(gs(o[3]));
                        outToFlight.writeObject(pricequery);
                        String price = inFromFlight.readLine();
                        outToFlight.writeObject(message);
                        if(inFromFlight.readLine().equals("true")){
                            cust.reserve( key, gs(o[3]), Integer.valueOf(price));      
                            rm_server.writeData( gi(o[1]), cust.getKey(), cust );
                            outToClient.println("true");
                        } else {
                            outToClient.println("false");
                        }
                    }
                    break;
                case "reservecar": //18
                    Customer cust_car = (Customer) rm_server.readData(gi(o[1]), Customer.getKey(gi(o[2])));
                    String key_car = ("car-" + gs(o[3])).toLowerCase(); 
                    if ( cust_car == null ) {
                        Trace.warn("RM::reserveCar( " + gi(o[1]) + ", " + gi(o[2]) + ", " + key_car + ", "+ gi(o[3])+")  failed--customer doesn't exist" );
                        outToClient.println("false");
                        break;
                    } else {
                        Vector pricequery = new Vector();
                        pricequery.addElement("querycarprice");
                        pricequery.addElement(gs(o[1]));
                        pricequery.addElement(gs(o[3]));
                        outToCar.writeObject(pricequery);
                        String price = inFromCar.readLine();
                        outToCar.writeObject(message);
                        if(inFromCar.readLine().equals("true")){
                            cust_car.reserve( key_car, gs(o[3]), Integer.valueOf(price));      
                            rm_server.writeData( gi(o[1]), cust_car.getKey(), cust_car);
                            outToClient.println("true");
                        } else {
                            outToClient.println("false");
                        }
                    }
                    break;
                case "reserveroom": //19
                    Customer cust_room = (Customer) rm_server.readData(gi(o[1]), Customer.getKey(gi(o[2])));
                    String key_room = ("room-" + gs(o[3])).toLowerCase(); 
                    if ( cust_room == null ) {
                        Trace.warn("RM::reserveRoom( " + gi(o[1]) + ", " + gi(o[2]) + ", " + key_room + ", "+ gi(o[3])+")  failed--customer doesn't exist" );
                        outToClient.println("false");
                        break;
                    } else {
                        Vector pricequery = new Vector();
                        pricequery.addElement("queryroomprice");
                        pricequery.addElement(gs(o[1]));
                        pricequery.addElement(gs(o[3]));
                        outToRoom.writeObject(pricequery);
                        String price = inFromRoom.readLine();
                        outToRoom.writeObject(message);
                        if(inFromRoom.readLine().equals("true")){
                            cust_room.reserve( key_room, gs(o[3]), Integer.valueOf(price));      
                            rm_server.writeData( gi(o[1]), cust_room.getKey(), cust_room);
                            outToClient.println("true");
                        } else {
                            outToClient.println("false");
                        }
                    }
                    break;
                case "itinerary": //20
                    Vector flightNumbers = new Vector();
                    int n = o.length;
                    for(int i=0;i<n-6;i++)
                        flightNumbers.addElement(o[3+i]);
                    if (flightNumbers.size()==0) {
                        outToClient.println("false");
                        break;
                    }
                    Customer cust_it = (Customer) rm_server.readData(gi(o[1]), Customer.getKey(gi(o[2])));
                    if (cust_it == null) {
                        outToClient.println("false");
                        break;
                    }
                    Hashtable<Integer,Integer> f_cnt = new Hashtable<Integer,Integer>();
                    int[] flights = new int[flightNumbers.size()];
                    for (int i = 0; i < flightNumbers.size(); i++) {
                        try {
                            flights[i] = gi(flightNumbers.elementAt(i));
                        }
                        catch (Exception e){}
                    }
                    for (int i = 0; i < flightNumbers.size(); i++) {
                        if (f_cnt.containsKey(flights[i]))
                            f_cnt.put(flights[i], f_cnt.get(flights[i])+1);
                        else
                            f_cnt.put(flights[i], 1);
                    }
                    if (gs(o[n-2]).equals("true")) {
                        Vector carnumquery = new Vector();
                        carnumquery.addElement("querycar");
                        carnumquery.addElement(gs(o[1]));
                        carnumquery.addElement(gs(o[n-3]));
                        outToCar.writeObject(carnumquery);
                        int carnum = Integer.parseInt(inFromCar.readLine());
                        if (carnum == 0 ) {
                            outToClient.println("false");
                            break;
                        }
                    }
                    if (gs(o[n-1]).equals("true")) {
                        Vector roomnumquery = new Vector();
                        roomnumquery.addElement("queryroom");
                        roomnumquery.addElement(gs(o[1]));
                        roomnumquery.addElement(gs(o[n-3]));
                        outToRoom.writeObject(roomnumquery);
                        int roomnum = Integer.parseInt(inFromRoom.readLine());
                        if (roomnum == 0 ) {
                            outToClient.println("false");
                            break;
                        }
                    }
                    Set<Integer> keys = f_cnt.keySet();
                    for (int k_e_y : keys) {
                        Vector flightnumquery = new Vector();
                        flightnumquery.addElement("queryflight");
                        flightnumquery.addElement(gs(o[1]));
                        flightnumquery.addElement(String.valueOf(k_e_y));
                        outToFlight.writeObject(flightnumquery);
                        int flightcnt = Integer.parseInt(inFromFlight.readLine());
                        if (flightcnt < f_cnt.get(k_e_y) ) {
                            outToClient.println("false");
                            break oloop;
                        }
                    }
                    // reserve a car
                    if (gs(o[n-2]).equals("true")) {
                        String key_car_ = ("car-" + gs(o[n-3])).toLowerCase(); 
                        Vector pricequery = new Vector();
                        pricequery.addElement("querycarprice");
                        pricequery.addElement(gs(o[1]));
                        pricequery.addElement(gs(o[n-3]));
                        outToCar.writeObject(pricequery);
                        String price = inFromCar.readLine();
                        Vector reserveacar = new Vector();
                        reserveacar.addElement("reservecar");
                        reserveacar.addElement(gs(o[1]));
                        reserveacar.addElement(gs(o[2]));
                        reserveacar.addElement(gs(o[n-3]));
                        outToCar.writeObject(reserveacar);
                        if(inFromCar.readLine().equals("true")){
                            cust_it.reserve( key_car_, gs(o[n-3]), Integer.valueOf(price));      
                            rm_server.writeData( gi(o[1]), cust_it.getKey(), cust_it);
                        } else {
                            outToClient.println("false");
                            break;
                        }
                    }
                    // reserve a room
                    if (gs(o[n-1]).equals("true")) {
                        String key_room_ = ("room-" + gs(o[n-3])).toLowerCase(); 
                        Vector pricequery = new Vector();
                        pricequery.addElement("queryroomprice");
                        pricequery.addElement(gs(o[1]));
                        pricequery.addElement(gs(o[n-3]));
                        outToRoom.writeObject(pricequery);
                        String price = inFromRoom.readLine();
                        Vector reservearoom = new Vector();
                        reservearoom.addElement("reserveroom");
                        reservearoom.addElement(gs(o[1]));
                        reservearoom.addElement(gs(o[2]));
                        reservearoom.addElement(gs(o[n-3]));
                        outToRoom.writeObject(reservearoom);
                        if(inFromRoom.readLine().equals("true")){
                            cust_it.reserve( key_room_, gs(o[n-3]), Integer.valueOf(price));      
                            rm_server.writeData( gi(o[1]), cust_it.getKey(), cust_it);
                        } else {
                            outToClient.println("false");
                            break;
                        }
                    }
                    for (int i = 0; i< flightNumbers.size() ;i++ ) {
                        int f_num = Integer.parseInt((String)flightNumbers.elementAt(i));
                        String key_flight = ("flight-" + f_num).toLowerCase(); 
                        Vector pricequery = new Vector();
                        pricequery.addElement("queryflightprice");
                        pricequery.addElement(gs(o[1]));
                        pricequery.addElement(String.valueOf(f_num));
                        outToFlight.writeObject(pricequery);
                        String price = inFromFlight.readLine();
                        Vector reserveaflight = new Vector();
                        reserveaflight.addElement("reserveflight");
                        reserveaflight.addElement(gs(o[1]));
                        reserveaflight.addElement(gs(o[2]));
                        reserveaflight.addElement(String.valueOf(f_num));
                        outToFlight.writeObject(reserveaflight);
                        if(inFromFlight.readLine().equals("true")){
                            cust_it.reserve( key_flight, String.valueOf(f_num), Integer.valueOf(price));      
                            rm_server.writeData( gi(o[1]), cust_it.getKey(), cust_it);
                        } else {
                            outToClient.println("false");
                            break oloop;
                        }
                    }
                    outToClient.println("true");
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
