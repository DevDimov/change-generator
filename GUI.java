import java.text.DecimalFormat;
import java.util.HashMap;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {
	
	ChangeGenerator cg = new ChangeGenerator();
	DecimalFormat df = new DecimalFormat("0.00");
	
	Button[] activeMenuButton = new Button[1];
	Button[] activePreferredCoin = new Button[1];
	Button[] activeExcludedCoin = new Button[1];
	
	HashMap<String, Double> previousWindowSize = new HashMap<>(2);
	
	public Button createMenuButton(String text) {
		Button button = new Button();       
		button.setText(text);
		button.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 20));
		button.setMinSize(400,80);
		button.setMaxWidth(1000d);
		button.getStyleClass().add("menu-button");
		return button;
	}
	
	public Button createGenerateButton(String text) {
		Button button = new Button();       
		button.setText(text);
		button.setPadding(new Insets (0,0,4,0));
		button.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 16));
		button.setMinSize(200,40);
		button.getStyleClass().add("generate-button");
		return button;
	}
	
	public Button createPreferredCoinButton(String text) {
		Button button = new Button();       
		button.setText(text);
		button.setPadding(new Insets (0,0,4,0));
		button.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 20));
		button.setMinSize(60,50);
		button.getStyleClass().add("preferred-coin-button");
		button.setOnAction(e -> togglePreferredCoin(button));
		return button;
	}
	
	public Button createExcludeCoinButton(String text) {
		Button button = new Button();       
		button.setText(text);
		button.setPadding(new Insets (0,0,4,0));
		button.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 20));
		button.setMinSize(60,50);
		button.getStyleClass().add("exclude-coin-button");
		button.setOnAction(e -> toggleExcludedCoin(button));
		return button;
	}
	
	public TextField createTextField() {
		TextField textfield = new TextField();
		textfield.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 20));
		textfield.setAlignment(Pos.CENTER_RIGHT);
		textfield.setPadding(new Insets (0,8,0,0));
		textfield.setMinSize(140, 50);
		textfield.setMaxSize(140, 50);
		return textfield;
	}
	
	public void changeScene(BorderPane borderPane, HBox hbox, VBox vbox, Button menuButton, Stage stage, Scene scene) {
		borderPane.setTop(hbox);
		borderPane.setCenter(vbox);
		toggleMenuButton(menuButton);
		stage.setHeight(previousWindowSize.get("Height"));
		stage.setWidth(previousWindowSize.get("Width"));
		stage.setScene(scene);
	}
	
	public void addInputLimiter(TextField textfield) {
		textfield.textProperty().addListener((observable, oldValue, newValue) -> {
			// digits of length 0 to 2
        	// may be followed by a single dot
        	// and up to two decimal places
        	if (!newValue.matches("\\d{0,3}([\\.]\\d{0,2})?")) {
        		textfield.setText(oldValue);
            }	
        	try {
        		if (Double.valueOf(newValue) > 100.00) {
        			textfield.setText(oldValue);
    			}
        	}
        	catch (NumberFormatException e) {
        	}
		});
	}
	
	public int toPence(String input) {
		Double amount = Double.valueOf(input) * 100;
		return amount.intValue();
	}
	
	public String toPounds(int value) {
		if (value == 200) {
			return "£2";
		}
		if (value == 100) {
			return "£1";
		}
		if (value == 50) {
			return "50p";
		}
		if (value == 20) {
			return "20p";
		}
		if (value == 10) {
			return "10p";
		}
		return null;
	}
	
	public Boolean inputAmountIsValid(String input) {
		if (Double.valueOf(input) > 100.00) {
			return false;
		}
		return true;
	}
	
	public String getPreferredCoin() {
		return activePreferredCoin[0].getText();
	}
	
	public int getPreferredCoinInPence() {
		String coinName = activePreferredCoin[0].getText();
		if (coinName.equals("£2")) {
			return 200;
		}
		if (coinName.equals("£1")) {
			return 100;
		}
		if (coinName.equals("50p")) {
			return 50;
		}
		if (coinName.equals("20p")) {
			return 20;
		}
		if (coinName.equals("10p")) {
			return 10;
		}
		return -1;
	}
	
	public String getExcludedCoin() {
		return activeExcludedCoin[0].getText();
	}
	
	public int getExcludedCoinInPence() {
		String excludedCoin = activeExcludedCoin[0].getText();
		if (excludedCoin.equals("£2")) {
			return 200;
		}
		if (excludedCoin.equals("£1")) {
			return 100;
		}
		if (excludedCoin.equals("50p")) {
			return 50;
		}
		if (excludedCoin.equals("20p")) {
			return 20;
		}
		if (excludedCoin.equals("10p")) {
			return 10;
		}
		return -1;
	}
	
	public String getSingleCoinChangeAsString(int[] change) {
		if (change[0] == 0) {
			return "Your preferred coin is bigger than the amount inserted";
		}
		String result = "Your change is: " + change[0] + " x " + getPreferredCoin();
		if (change[1] != 0) {
			result += " with a remainder of " + change[1] + "p";
		}
		return result;
	}
	
	public String getMultiCoinChangeAsString (HashMap<Integer,Integer> change) {
		String result = "";
		String remainder = "";
		for (Integer i : change.keySet()) {
			if (i==0) {
				remainder = ", and a remainder of " + change.get(i) + "p";
				continue;
			}
			if (i != 0 && result.equals("")) {
				result += "Your change is: " + change.get(i) + " x " + toPounds(i);
				continue;
			}
			if (i != 0 && !result.equals("")) {
				result += ", " + change.get(i) + " x " + toPounds(i);
			}
		}
		return result + remainder;
	}
	
	public void toggleMenuButton(Button button) {
		activeMenuButton[0].getStyleClass().remove("menu-button-active");
		activeMenuButton[0].getStyleClass().add("menu-button");
		button.getStyleClass().add("menu-button-active");
		activeMenuButton[0] = button;
	}
	
	public void togglePreferredCoin(Button button) {
		activePreferredCoin[0].getStyleClass().remove("preferred-coin-button-active");
		activePreferredCoin[0].getStyleClass().add("preferred-coin-button");
		button.getStyleClass().add("preferred-coin-button-active");
		activePreferredCoin[0] = button;
	}
	
	public void toggleExcludedCoin(Button button) {
		activeExcludedCoin[0].getStyleClass().remove("exclude-coin-button-active");
		activeExcludedCoin[0].getStyleClass().add("exclude-coin-button");
		button.getStyleClass().add("exclude-coin-button-active");
		activeExcludedCoin[0] = button;
	}
	
	public Label createLabel(String text) {
		Label label = new Label(text);
		label.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 16));
		return label;
	}
	
	public void showMaxAmountError(Alert a) {
       a.setAlertType(AlertType.ERROR);
       a.setHeaderText("Please insert up to £100.00 only");
       a.show();
    }
	
	public void captureWindowSize(Stage stage) {
		previousWindowSize.put("Height", stage.getHeight());
		previousWindowSize.put("Width", stage.getWidth());
	}
	
	public void start(Stage stage) {
		
		Button singleCoinMenuButton, multiCoinMenuButton;
		HBox menuHBox = new HBox();
		Label singleCoinInsertLabel, preferredCoinLabel, singleCoinResultLabel;
		Label multiCoinInsertLabel, excludeCoinLabel, multiCoinResultLabel;
		TextField singleCoinInput, multiCoinInput;
		Button generateSingleCoinChange, generateMultiCoinChange;
		Alert a = new Alert(AlertType.NONE);
		VBox singleCoinVBox, multiCoinVBox;
		BorderPane borderSingleCoin, borderMultiCoin;
		Scene singleCoinScene, multiCoinScene;
		
		// SINGLE-COIN SCENE
		
		singleCoinMenuButton = createMenuButton("Single-Coin Change");
		multiCoinMenuButton = createMenuButton("Multi-Coin Change");
		
		menuHBox.setStyle("-fx-background-color: #2D2A27;");
		menuHBox.getChildren().addAll(singleCoinMenuButton, multiCoinMenuButton);
		HBox.setHgrow(singleCoinMenuButton, Priority.ALWAYS);
		HBox.setHgrow(multiCoinMenuButton, Priority.ALWAYS);
		
		singleCoinInsertLabel = createLabel("Insert amount (up to £100)");
		Label poundSymbol = new Label("£");
		poundSymbol.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 20));
		poundSymbol.setPadding(new Insets (0,0,0,5));
		singleCoinInput = createTextField();
		addInputLimiter(singleCoinInput);
		StackPane singleCoinInputStack = new StackPane();
		singleCoinInputStack.getChildren().addAll(singleCoinInput, poundSymbol);
		singleCoinInputStack.setMinSize(120, 50);
		singleCoinInputStack.setMaxSize(120, 50);
		StackPane.setAlignment(poundSymbol, Pos.CENTER_LEFT);
		
		preferredCoinLabel = createLabel("Choose preferred coin");
		Button twoPoundButtonSingleCoin = createPreferredCoinButton("£2");
		twoPoundButtonSingleCoin.getStyleClass().remove("preferred-coin-button");
		twoPoundButtonSingleCoin.getStyleClass().add("preferred-coin-button-active");
		activePreferredCoin[0] = twoPoundButtonSingleCoin;
		
		HBox preferredCoinHBox = new HBox();
		preferredCoinHBox.getChildren().add(twoPoundButtonSingleCoin);
		for (int i=1; i<5; i++) {
			Button button = createPreferredCoinButton(cg.getCoinListString()[i]);
			preferredCoinHBox.getChildren().add(button);
		}
		preferredCoinHBox.setAlignment(Pos.CENTER);
		
		generateSingleCoinChange = createGenerateButton("Generate change");
		singleCoinResultLabel = createLabel("");
		
		singleCoinVBox = new VBox();
		singleCoinVBox.getChildren().addAll(singleCoinInsertLabel, singleCoinInputStack, preferredCoinLabel,
											preferredCoinHBox, generateSingleCoinChange, singleCoinResultLabel);
		singleCoinVBox.setAlignment(Pos.CENTER);
		VBox.setMargin(singleCoinInputStack, new Insets(20, 0, 0, 0));
		VBox.setMargin(preferredCoinLabel, new Insets(50, 0, 10, 0));
		VBox.setMargin(generateSingleCoinChange, new Insets(45, 0, 45, 0));
		
		borderSingleCoin = new BorderPane();
		borderSingleCoin.setTop(menuHBox);
		borderSingleCoin.setCenter(singleCoinVBox);
		
		singleCoinScene = new Scene(borderSingleCoin);
		singleCoinScene.getStylesheets().add("file:resources/style.css");

		singleCoinMenuButton.setOnAction(e -> {	
			captureWindowSize(stage);
			changeScene(borderSingleCoin, menuHBox, singleCoinVBox, singleCoinMenuButton, stage, singleCoinScene);
		});
		
		// MULTI-COIN SCENE
		
		borderMultiCoin = new BorderPane();
		multiCoinVBox = new VBox();
		
		multiCoinScene = new Scene(borderMultiCoin);
		multiCoinScene.getStylesheets().add("file:resources/style.css");
		
		multiCoinInsertLabel = createLabel("Insert amount (up to £100)");
		Label poundSymbolMultiCoin = new Label("£");
		poundSymbolMultiCoin.setFont(Font.loadFont("file:resources/fonts/GiraSans-Regular.ttf", 20));
		poundSymbolMultiCoin.setPadding(new Insets (0,0,0,5));
		multiCoinInput = createTextField();
		addInputLimiter(multiCoinInput);
		StackPane multiCoinInputStack = new StackPane();
		multiCoinInputStack.getChildren().addAll(multiCoinInput, poundSymbolMultiCoin);
		multiCoinInputStack.setMinSize(120, 50);
		multiCoinInputStack.setMaxSize(120, 50); 
		StackPane.setAlignment(poundSymbolMultiCoin, Pos.CENTER_LEFT);

		excludeCoinLabel = createLabel("Choose coin to exclude");
		
		Button twoPoundButtonMultiCoin = createExcludeCoinButton("£2");
		twoPoundButtonMultiCoin.getStyleClass().remove("exclude-coin-button");
		twoPoundButtonMultiCoin.getStyleClass().add("exclude-coin-button-active");
		activeExcludedCoin[0] = twoPoundButtonMultiCoin;
		
		HBox excludeCoinHBox = new HBox();
		excludeCoinHBox.getChildren().add(twoPoundButtonMultiCoin);
		for (int i=1; i<5; i++) {
			Button button = createExcludeCoinButton(cg.getCoinListString()[i]);
			excludeCoinHBox.getChildren().add(button);
		}
		excludeCoinHBox.setAlignment(Pos.CENTER);
		
		generateMultiCoinChange = createGenerateButton("Generate change");
		multiCoinResultLabel = createLabel("");
		
		multiCoinVBox.getChildren().addAll(multiCoinInsertLabel, multiCoinInputStack, excludeCoinLabel,
				excludeCoinHBox, generateMultiCoinChange, multiCoinResultLabel);
		multiCoinVBox.setAlignment(Pos.CENTER);
		VBox.setMargin(multiCoinInputStack, new Insets(20, 0, 0, 0));
		VBox.setMargin(excludeCoinLabel, new Insets(50, 0, 10, 0));
		VBox.setMargin(generateMultiCoinChange, new Insets(45, 0, 45, 0));
		
		multiCoinMenuButton.setOnAction(e -> {
			captureWindowSize(stage);
			changeScene(borderMultiCoin, menuHBox, multiCoinVBox, multiCoinMenuButton, stage, multiCoinScene);
		});
		
		// BUTTON FUNCTIONALITY AND INITIAL VALUES
		
		singleCoinMenuButton.getStyleClass().remove("menu-button");
		singleCoinMenuButton.getStyleClass().add("menu-button-active");
		activeMenuButton[0] = singleCoinMenuButton;
		
		generateSingleCoinChange.setOnAction(e -> {
			if (!singleCoinInput.getText().isEmpty()) {
				String amount_str = singleCoinInput.getText();
				if (!inputAmountIsValid(amount_str)) {
					showMaxAmountError(a);
				}
				else {
					int[] change = cg.generateSingleCoinChange(toPence(amount_str), getPreferredCoinInPence());
					singleCoinResultLabel.setText(getSingleCoinChangeAsString(change));
				}
			}
		});
		
		generateMultiCoinChange.setOnAction(e -> {
			if (!multiCoinInput.getText().isEmpty()) {
				String amount_str = multiCoinInput.getText();
					if (!inputAmountIsValid(amount_str)) {
						showMaxAmountError(a);
					}
					else {
						HashMap<Integer,Integer> change = cg.generateMultiCoinChange(toPence(amount_str), getExcludedCoinInPence());
						multiCoinResultLabel.setText(getMultiCoinChangeAsString(change));
					}
			}
		});
		
		stage.setTitle("Change Generator");
		stage.setScene(singleCoinScene);
		stage.show();
		stage.setMinWidth(816);
		stage.setMinHeight(650);
	}
	
	public static void main(String[] args) {
		Application.launch(GUI.class, args);
	}
}
