package com.mauersu.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.HostAndPort;

public class ClusterServerInfo implements ServerInfo, Serializable {

	private static final long serialVersionUID = -2830775587934411870L;
	private String name;
	private List<HostAndPort> servers;
	private String password;

	public ClusterServerInfo() {
	}

	public ClusterServerInfo(String name, String password, String hosts) {
		this.name = name;
		this.password = password;

		String[] hostsArr = hosts.split(",", -1);
		this.servers = new ArrayList<HostAndPort>();
		for (String str : hostsArr) {
			String[] hostAndPort = str.split(":", -1);
			servers.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
		}
	}

	public ClusterServerInfo(String name, List<HostAndPort> servers, String password) {
		this.name = name;
		this.servers = servers;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<HostAndPort> getServers() {
		return servers;
	}

	public void setServers(List<HostAndPort> servers) {
		this.servers = servers;
	}

}
