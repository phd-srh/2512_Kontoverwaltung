package controller;

import dao.KontoDAO;
import dao.TempDAO;
import model.Konto;
import view.MainView;
import view.NeuesKontoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainController {
    private MainView mainView;
    private KontoDAO kontoDB;

    public MainController(MainView mainView, KontoDAO kontoDB) {
        this.mainView = mainView;
        this.kontoDB = kontoDB;

        mainView.setNeuesKontoButtonListener( this::performNeuesKonto );
        mainView.setKontoAnzeigenButtonListener( this::performKontoAnzeigen );

    }

    private void performKontoAnzeigen(ActionEvent actionEvent) {
        try {
            int kontonummer = mainView.getKontonummer();
            Konto konto = kontoDB.getKontoByKontonummer(kontonummer);
            mainView.setKontoinhaber( konto.getInhaber() );
            mainView.setKontostand( konto.getKontostand() );
        }
        catch (Exception e){
            mainView.setKontoinhaber("Konto nicht vorhanden!");
            mainView.setKontostand(0.0);
        }

    }

    private void performNeuesKonto(ActionEvent actionEvent) {
        NeuesKontoView neuesKontoView = new NeuesKontoView();
        mainView.setEnabled(false);

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

    public static void main(String[] args) {
        new MainController( new MainView(), new TempDAO() );
    }
}
