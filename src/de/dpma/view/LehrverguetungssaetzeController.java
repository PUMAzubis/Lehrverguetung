package de.dpma.view;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.dpma.FXML_GUI;
import de.dpma.dao.StundenlohnDAO;
import de.dpma.model.Stundenlohn;
import de.dpma.util.AlertUtil;
import de.dpma.util.DataChecker;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class LehrverguetungssaetzeController {

	@FXML
	TextField verguetungsSatzTextField = new TextField();

	StundenlohnDAO stundenlohnDAO;

	Stundenlohn stundenlohn;

	KeyEvent keyEvent;

	AlertUtil alert;

	@FXML
	public void initialize() {

		stundenlohn = new Stundenlohn();
		try {
			MainPageController.stundenlohnDAO
					.selectStundenlohn(MainPageController.stundenlohnDAO.selectAllStundenloehne().size());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void handleSubmit() throws SQLException, ParseException {

		if (!DataChecker.isNumeric(verguetungsSatzTextField.getText())) {
			alert = new AlertUtil("Invalider Vergütungssatz",
					"Der Vergüngssatz ist entweder nicht angegeben oder enthält Zeichen, welche nicht numerisch sind.",
					"WARN");
		} else if (!DataChecker.hasTwoOrNoDecimals(verguetungsSatzTextField.getText())) {
			alert = new AlertUtil("Invalider Vergütungssatz",
					"Der Vergüngssatz enthält ungültige Nachkommastellen. Erlaubt sind keine oder zwei Nachkommastellen.",
					"WARN");
		} else {
			double sdtLohn = Double.valueOf(verguetungsSatzTextField.getText().replace(",", "."));
			stundenlohn.setLohn(sdtLohn);

			if (stundenlohn.getId() == 0) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentTimestamp = sdf.format(System.currentTimeMillis());
				System.out.println(currentTimestamp);
				stundenlohn.setCreationTime(currentTimestamp);

				MainPageController.stundenlohnDAO.insertStundenlohn(this.stundenlohn);

			} else {
				MainPageController.stundenlohnDAO.updateStundenlohn(this.stundenlohn);
			}

			FXML_GUI.primaryStage.close();
		}

		// TODO: Tabelle aktualisieren

		// MainPageController tableReload = new MainPageController();
		// tableReload.handleSearch();
		// tableReload.insertIntoLehrvergueungssaetzeTable(stundenlohnDAO.selectAllStundenloehne());
		// tableReload = null;
		// System.gc();
	}

	@FXML
	public void handleCancel() {

		FXML_GUI.primaryStage.close();
	}

	public void handleNew(Stundenlohn stundenlohn) {

		this.stundenlohn = stundenlohn;

		String stdLohn = String.valueOf(stundenlohn.getLohn());
		verguetungsSatzTextField.setText(stdLohn);
	}

	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) throws ParseException {

		this.keyEvent = keyEvent;
		switch (keyEvent.getCode()) {
		case ENTER:
			try {
				handleSubmit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}
}
