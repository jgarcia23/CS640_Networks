package edu.wisc.cs.sdn.vnet.sw;

import java.util.HashMap;
import java.util.Map;
import net.floodlightcontroller.packet.MACAddress;
import net.floodlightcontroller.packet.Ethernet;
import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;

/**
 * @author Aaron Gember-Jacobson and Joseluis Garcia
 */
public class Switch extends Device {

	// create a dictionary/hashmap of the host and port number
	private Map<MACAddress, MACTable> macAddressMap = new HashMap<>();

	/**
	 * Creates a router for a specific host.
	 * 
	 * @param host
	 *            hostname for the router
	 */
	public Switch(String host, DumpFile logfile) {
		super(host, logfile);
	}

	private static class MACTable {

		Iface destination;
		long time;

		public MACTable(Iface destination) {
			this.destination = destination;
			this.time = System.currentTimeMillis();
		}

		// You should timeout learned MAC addresses after 15 seconds.
		public boolean timer() {
			long timer = System.currentTimeMillis() - this.time;
			return (timer) >= 1500;
		}
		
		public void reset(){
			this.time = System.currentTimeMillis();
		}
	}

	// An incoming interface on which packets for this VC arrive at the switch
	/**
	 * Handle an Ethernet packet received on a specific interface.
	 * 
	 * @param etherPacket
	 *            the Ethernet packet that was received
	 * @param inIface
	 *            the interface on which the packet was received
	 */
	public void handlePacket(Ethernet etherPacket, Iface inIface) {
		System.out.println("*** -> Received packet: "
				+ etherPacket.toString().replace("\n", "\n\t"));

		
		
		if(macAddressMap.containsKey(etherPacket.getSourceMAC())){
			macAddressMap.get(etherPacket.getSourceMAC()).reset();
		}
		else{
			macAddressMap.put(etherPacket.getSourceMAC(), new MACTable(inIface));
			
		}

		// Find destination MAC
		Iface dst = source(etherPacket.getDestinationMAC());
		if (dst != null) {
			sendPacket(etherPacket, dst);
		} else {
			flood(etherPacket, inIface);
		}

	}

	private void flood(Ethernet packet, Iface ignore) {
		for (Map.Entry<String, Iface> outgoing : getInterfaces().entrySet()) {
			if (!outgoing.getValue().equals(ignore)) {
				sendPacket(packet, outgoing.getValue());
			}
		}
	}

	// track the MAC addresses, and associated interfaces
	private Iface source(MACAddress addr) {
		if (macAddressMap.containsKey(addr)) {
			MACTable address = macAddressMap.get(addr);
			if (address.timer()) {
				macAddressMap.remove(addr);
				return null;
			}
			return address.destination;
		}
		return null;
	}

}
