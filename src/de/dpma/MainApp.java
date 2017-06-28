package de.dpma;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

import de.dpma.dao.DatabaseConnection;
import de.dpma.util.AlertUtil;
import de.dpma.util.StringIndexOf;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	public static DatabaseConnection dbcon;
	
	public static boolean dbcon_error;
	
	Logger log = Logger.getLogger(MainApp.class.getName());
	
	public static int counter = 0;
	
	static FXMLLoader loader;
	
	static AlertUtil alert;
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			FXML_GUI fxml_gui = new FXML_GUI();
			fxml_gui.primaryStage[counter] = primaryStage;
			fxml_gui.initRootLayout(false);
			fxml_gui.showMainPage();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		boolean b;
		if (!(b = (new File("Vorlagen\\").exists()))) {
			b = (new File("Vorlagen\\").mkdirs());
		}
		
		if (!new File("Vorlagen\\Rechnungsbegleitblatt.docx").exists()) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					
					alert = new AlertUtil("Vorlage fehlt",
							"Die Vorlage 'Rechnungsbegleitblatt.docx' wurde nicht im Ordner 'Vorlagen' gefunden! Ohne diese Vorlage mit exakt diesem Namen ist ein Programmstart nicht m�glich.",
							"ERROR");
				}
			});
		}
		else if (!new File("Vorlagen\\Rechnungsbegleitblatt.docx").exists()) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					
					alert = new AlertUtil("Vorlage fehlt",
							"Die Vorlage 'Auszahlung.docx' wurde nicht im Ordner 'Vorlagen' gefunden! Ohne diese Vorlage mit exakt diesem Namen ist ein Programmstart nicht m�glich.",
							"ERROR");
				}
			});
		}
		else {
			dbcon_error = false;
			
			try {
				dbcon = new DatabaseConnection();
				
				launch(args);
				
			}
			catch (ClassNotFoundException |
					
					SQLException e) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						
						Throwable cause = e.getCause();
						String causeString;
						String causeStringPlain;
						final StringWriter sw = new StringWriter();
						final PrintWriter pw = new PrintWriter(sw, true);
						cause.printStackTrace(pw);
						causeStringPlain = sw.getBuffer().toString();
						causeString = causeStringPlain.substring(0, Math.min(causeStringPlain.length(), 11));
						causeString = causeString.substring(causeString.indexOf(" ")).substring(1);
						
						if (causeString.equals("XJ004")) {
							alert = new AlertUtil("Schwerwiegender Fehler",
									"Es konnte keine Verbindung zur Datenbank hergestellt werden, da sie nicht gefunden werden konnte. Im Programmverzeichnis muss der Ordner 'Datenbank' liegen.",
									"ERROR");
							
						}
						else if (causeString.equals("XJ040") && StringIndexOf.count(causeStringPlain, "ERROR") == 2) {
							alert = new AlertUtil("Problem bei Programmstart",
									"Das Programm konnte nicht gestartet werden, da es gerade von einem anderen Benutzer verwendet wird.",
									"WARNING");
						}
						else {
							alert = new AlertUtil("Schwerwiegender Fehler",
									"Beim Aufbau der Datenbankverbindung ist ein unbekanntes Problem augetreten.",
									"ERROR");
						}
					}
				});
				
				dbcon_error = true;
			}
			finally {
				if (!dbcon_error) {
					try {
						dbcon.closeConnection();
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
