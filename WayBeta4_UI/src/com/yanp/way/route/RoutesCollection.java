package com.yanp.way.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.yanp.way.Constants;
import com.yanp.way.R;
/**
 * (Singleton) Manage all the routes saved on the app.
 * This list is saved in a file, on the phone.
 * @author YPierru
 *
 */
public class RoutesCollection extends ArrayList<Route> implements Serializable {

	
	private static final long serialVersionUID = 1L;

	private static RoutesCollection INSTANCE;

	public static RoutesCollection getInstance() {
		INSTANCE = loadRoutesCollection();
		if (INSTANCE == null) {
			INSTANCE = new RoutesCollection();
			INSTANCE.saveRoutesCollection();
		}
		return INSTANCE;
	}

	private RoutesCollection() {
		super();
	}
	
	/**
	 * Check if name is already the name of another route
	 * @param name
	 * @return
	 */
	public boolean isNameAlreadyPresent(String name){
		for(int i=0;i<this.size();i++){
			if(this.get(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the array of all the route's name
	 * @return
	 */
	public String[] getRouteNameList(){
		String rtr[] = new String[this.size()];
		for(int i=0;i<this.size();i++){
			if(this.get(i).isValidate()){
				rtr[i]=this.get(i).getName();
			}else{
				rtr[i]="(en cours) "+this.get(i).getName();
			}
		}
		return rtr;
	}
	
	/**
	 * Return the list of all the routes.
	 * @return
	 */
	public ArrayList<Route> getListRoutes(){
		ArrayList<Route> listRoute = new ArrayList<Route>();
		for(int i=0;i<this.size();i++){
				listRoute.add(this.get(i));
		}
		return listRoute;
	}
	
	
	/**
	 * Remove "route" from the list
	 * @param route
	 * @return
	 */
	public boolean remove(Route route) {
		for(int i=0;i<this.size();i++){
			if(this.get(i).getIdHash()==route.getIdHash()){
				this.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the route identified by the hash id
	 * @param id
	 * @return the route found, null if there's no route matching
	 */
	public Route getByHashId(int id) {
		Route tj = null;
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash() == id) {
				tj = this.get(i);
			}
		}

		return tj;
	}

	/**
	 * Replace the route "route" in a list
	 * @param route
	 * @return true if the route has been replaced, false if not.
	 */
	public boolean replace(Route route) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash()==route.getIdHash()) {
				this.remove(i);
				this.add(i, route);
				return true;
			}
		}
		return false;
	}

	/**
	 * Save the list in a file.
	 */
	public void saveRoutesCollection() {

		File f = new File(Constants.PATH_FILE_ROUTES+R.string.file_route_name);

		try {
			ObjectOutputStream ooStream = new ObjectOutputStream(
					new FileOutputStream(f));
			ooStream.writeObject(this);
			ooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Load the list from a file
	 * @return
	 */
	public static RoutesCollection loadRoutesCollection() {

		File f = new File(Constants.PATH_FILE_ROUTES+R.string.file_route_name);
		RoutesCollection at = null;
		try {
			ObjectInputStream oiStream = new ObjectInputStream(
					new FileInputStream(f));
			at = (RoutesCollection) oiStream.readObject();
			oiStream.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return at;
	}

	/**
	 * Delete the file
	 */
	public void deleteRouteCollectionsFile() {
		File f = new File(Constants.PATH_FILE_ROUTES+R.string.file_route_name);
		if (f.exists()) {
			f.delete();
		}
	}
	
	/**
	 * Re-attribute the list index of a route.
	 */
	public void syncRouteIndex(){
		for(int i=0;i<this.size();i++){
			this.get(i).setIndexCollection(i);
		}
	}
}
