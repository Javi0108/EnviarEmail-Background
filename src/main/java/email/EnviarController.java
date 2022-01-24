package email;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class EnviarController implements Initializable {

	private EnviarModel model = new EnviarModel();

	public EnviarController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/View.fxml"));
		loader.setController(this);
		loader.load();
	}

	@FXML
	private BorderPane view;

	@FXML
	private TextField txtSMTP;

	@FXML
	private TextField txtRemitente;

	@FXML
	private TextField txtDestinatario;

	@FXML
	private TextField txtAsunto;

	@FXML
	private TextField txtPuerto;

	@FXML
	private TextArea txtMensaje;

	@FXML
	private PasswordField txtPass;

	@FXML
	private CheckBox checkSLL;

	@FXML
	private Button btEnviar;

	@FXML
	private Button btVaciar;

	@FXML
	private Button btCerrar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Bindings.bindBidirectional(model.smtpProperty(), txtSMTP.textProperty());
		Bindings.bindBidirectional(txtPuerto.textProperty(), model.puertoProperty(), new NumberStringConverter());
		Bindings.bindBidirectional(model.sslProperty(), checkSLL.selectedProperty());
		Bindings.bindBidirectional(model.remitenteProperty(), txtRemitente.textProperty());
		Bindings.bindBidirectional(model.contraseñaProperty(), txtPass.textProperty());
		Bindings.bindBidirectional(model.destinatarioProperty(), txtDestinatario.textProperty());
		Bindings.bindBidirectional(model.asuntoProperty(), txtAsunto.textProperty());
		Bindings.bindBidirectional(model.mensajeProperty(), txtMensaje.textProperty());

	}

	@FXML
	public void OnActionEnviar(ActionEvent e) throws EmailException{
		Email email = new SimpleEmail();

		email.setHostName(model.getSmtp());
		email.setSmtpPort(model.getPuerto());
		email.setAuthenticator(new DefaultAuthenticator(model.getRemitente(), model.getContraseña()));
		email.setSSLOnConnect(model.isSsl());
		email.setFrom(model.getRemitente());
		email.setSubject(model.getAsunto());
		email.setMsg(model.getMensaje());
		email.addTo(model.getDestinatario());

		Task<Email> tarea = new Task<Email>() {
			@Override
			protected Email call() throws Exception {
				email.send();
				return email;
			}
		};
		tarea.setOnSucceeded(e -> {
			Alert enviado = new Alert(AlertType.INFORMATION);
			enviado.setTitle("Mensaje enviado");
			enviado.setHeaderText("Mensaje enviado a '" + model.getDestinatario() + "'.");

			Stage stage = (Stage) enviado.getDialogPane().getScene().getWindow();
			stage.getIcons().setAll(App.getPrimaryStage().getIcons());

			enviado.showAndWait();
		});
		tarea.setOnFailed(e -> {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Error");
			error.setHeaderText("No se pudo enviar el email.");
			error.setContentText("Invalid message supplied");

			Stage stage = (Stage) error.getDialogPane().getScene().getWindow();
			stage.getIcons().setAll(App.getPrimaryStage().getIcons());

			error.showAndWait();
		});
	}

	@FXML
	public void OnActionVaciar(ActionEvent v) {
		model.setSmtp("");
		model.setPuerto(0);
		model.setSsl(false);
		model.setRemitente("");
		model.setContraseña("");
		model.setDestinatario("");
		model.setAsunto("");
		model.setMensaje("");
	}

	@FXML
	public void OnActionCerrar(ActionEvent c) {
		Platform.exit();
	}

	public BorderPane getView() {
		return view;
	}

	public void setView(BorderPane view) {
		this.view = view;
	}

}
