package com.yanpierru.way.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

import com.yanpierru.way.Constantes;

/*
 * Singleton Classe fille d'ArrayList<Trajet>, permet de manipuler la liste de tous les trajets sauvegard??es
 * Certains m??thodes ont ????t???? r????crites.
 * Possibilit?? de sauvegarder dans un fichier cet objet.
 */
public class RoutesCollection extends ArrayList<Route> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static RoutesCollection INSTANCE;//Singleton

	//R??cup??ration de l'instance
	public static RoutesCollection getInstance() {
		INSTANCE = loadAllTrajet();
		if (INSTANCE == null) {
			INSTANCE = new RoutesCollection();
			INSTANCE.saveRoutesCollection();
		}
		return INSTANCE;
	}

	private RoutesCollection() {
		super();
	}

	public static void delete(){
		File f = new File(Constantes.PATH_FILE_ROUTES+Constantes.NAME_FILE_ROUTES);
		f.delete();
	}
	
	public boolean nameExists(String n){
		for(int i=0;i<this.size();i++){
			if(this.get(i).getName().equals(n)){
				return true;
			}
		}
		
		return false;
	}
	
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
	
	public ArrayList<Route> getListRoutes(){
		ArrayList<Route> listRoute = new ArrayList<Route>();
		for(int i=0;i<this.size();i++){
			//if(this.get(i).getTypeRoute().equals("VOITURE")){
				listRoute.add(this.get(i));
			//}
		}
		return listRoute;
	}
	
	/*public ArrayList<Route> getListRoutesCoureur(){
		ArrayList<Route> listRouteCoureur = new ArrayList<Route>();
		for(int i=0;i<this.size();i++){
			if(this.get(i).getTypeRoute().equals("COUREUR")){
				listRouteCoureur.add(this.get(i));
			}
		}
		return listRouteCoureur;
	}*/

	public boolean remove(Route t) {
		for(int i=0;i<this.size();i++){
			if(this.get(i).getIdHash()==t.getIdHash()){
				this.remove(i);
				return true;
			}
		}
		return false;
	}

	public Route getByHashId(int id) {
		Route tj = null;
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash() == id) {
				tj = this.get(i);
			}
		}

		return tj;
	}

	public boolean replace(Route tj) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getIdHash()==tj.getIdHash()) {
				this.remove(i);
				this.add(i, tj);
				return true;
			}
		}
		return false;
	}

	public void saveRoutesCollection() {

		File f = new File(Constantes.PATH_FILE_ROUTES+Constantes.NAME_FILE_ROUTES);

		try {
			ObjectOutputStream ooStream = new ObjectOutputStream(
					new FileOutputStream(f));
			ooStream.writeObject(this);
			ooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static RoutesCollection loadAllTrajet() {

		File f = new File(Constantes.PATH_FILE_ROUTES+Constantes.NAME_FILE_ROUTES);
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

	public void deleteFile() {
		File f = new File(Constantes.PATH_FILE_ROUTES+Constantes.NAME_FILE_ROUTES);
		if (f.exists()) {
			f.delete();
		}
	}
	
	public void syncRouteIndex(){
		for(int i=0;i<this.size();i++){
			this.get(i).setIndexCollection(i);
		}
	}
}
