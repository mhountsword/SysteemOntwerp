package nl.saxion.Models.observer;

import java.util.ArrayList;

public class Observer {

    private ArrayList<Updater> observers = new ArrayList<>();
    int spoolchanges = 0;

    int totalprint = 0;

    public void subscribe(Updater observer){
        observers.add(observer);
    }

    public void unsubscribe(Updater observer){
        observers.remove(observer);
    }

    public void addspool() {
        spoolchanges++;
        notifyobservers();
    }

    public void addprints() {
       totalprint++;
        notifyobservers();
    }

    public void notifyobservers(){
        for (Updater observer: observers){
            observer.update(spoolchanges,totalprint);
        }

    }


}
