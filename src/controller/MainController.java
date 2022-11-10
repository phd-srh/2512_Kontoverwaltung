package controller;

import dao.KontoDAO;
import dao.TempDAO;
import model.Festzinskonto;
import model.Giro;
import model.Konto;
import model.Sparkonto;
import view.MainView;
import view.NeuesKontoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainController {
    private MainView mainView;
    private KontoDAO kontoDB;
    private NeuesKontoView neuesKontoView;

    public MainController(MainView mainView, KontoDAO kontoDB) {
        this.mainView = mainView;
        this.kontoDB = kontoDB;

        mainView.setNeuesKontoButtonListener( this::performNeuesKonto );
        mainView.setKontoAnzeigenButtonListener( this::performKontoAnzeigen );
        mainView.setEinzahlenButtonListener( this::performEinzahlen );
        mainView.setAbhebenButtonListener( this::performAbheben );

    }

    private void performEinzahlen(ActionEvent actionEvent) {
        int kontonummer = mainView.getKontonummer();
        Konto konto = kontoDB.getKontoByKontonummer(kontonummer);
        if (konto != null) {
            konto.einzahlen( mainView.getBetrag() );
            kontoDB.updateKonto( kontonummer, konto );
        }
        else mainView.zeigeFehlermeldung("Konto nicht gefunden");
    }

    private void performAbheben(ActionEvent actionEvent) {
        int kontonummer = mainView.getKontonummer();
        Konto konto = kontoDB.getKontoByKontonummer(kontonummer);
        if (konto != null) {
            konto.abheben( mainView.getBetrag() );
            kontoDB.updateKonto( kontonummer, konto );
        }
        else mainView.zeigeFehlermeldung("Konto nicht gefunden");
    }

    private void performKontoAnzeigen(ActionEvent actionEvent) {
        int kontonummer = mainView.getKontonummer();

        Konto konto = kontoDB.getKontoByKontonummer(kontonummer);
        if (konto == null) {
            mainView.setKontoinhaber("");
            mainView.clearKontostand();
            mainView.zeigeFehlermeldung("Dieses Konto existiert nicht.");
        }
        else {
            mainView.setKontoinhaber(konto.getInhaber());
            mainView.setKontostand(konto.getKontostand());
        }
    }

    private void performNeuesKonto(ActionEvent actionEvent) {
        neuesKontoView = new NeuesKontoView();
        mainView.setEnabled(false);


        neuesKontoView.setAnlegenButtonListener(this::performKontoAnlegen);
        neuesKontoView.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {
                mainView.setEnabled(true);
                mainView.requestFocus();
            }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }

    private void performKontoAnlegen(ActionEvent actionEvent) {
        //System.out.println("Neues Konto");
        Konto k = null;
        int kontonummer = kontoDB.letzteAktuelleKontonummer() + 1;
        String name = neuesKontoView.getKontoinhaber();
        double limit = neuesKontoView.getKreditlimit();
        double zinssatz = neuesKontoView.getZinssatz();
        int laufzeit = neuesKontoView.getLaufzeit();

        switch (neuesKontoView.getKonto()){
            case 'G': k = new Giro(kontonummer, 0.0, name, limit);
            break;
            case 'S': k = new Sparkonto(kontonummer, 0.0, name, zinssatz);
            break;
            case 'F': k = new Festzinskonto(kontonummer, 0.0, name, zinssatz, laufzeit);
            break;
        }
        if (k != null) {
            if (!kontoDB.insertKonto(k))
                mainView.zeigeFehlermeldung("Konto kann nicht angelegt werden.");
        }

    }

    public static void main(String[] args) {
        new MainController( new MainView(), new TempDAO() );
    }
}
