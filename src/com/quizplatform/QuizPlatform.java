package com.quizplatform;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
public class QuizPlatform extends Application 
{
    private Stage primaryStage;
    private TextField usernameField;
    private PasswordField passwordField;
    private UserAuthenticator userAuthenticator;
    private List<Question> questions = new ArrayList<>();
    private VBox optionsLayout;
    private Scene optionsScene;
    private AtomicInteger score = new AtomicInteger(0);
    private List<String> userAnswers = new ArrayList<>();
    private AtomicInteger numCorrectAnswers = new AtomicInteger(0);
    public QuizPlatform() 
    {
        userAuthenticator = new UserAuthenticator();
    }
    public static void main(String[] args) 
    {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) 
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Quiz Platform");
        setupLoginScreen();
        primaryStage.show();
    }
    private void applyCSS(Scene scene) 
    {
        String cssPath = getClass().getResource("resources/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath); // Load CSS file
    }
    private void setupLoginScreen() 
    {
        Label titleLabel = new Label("Welcome to Quiz Platform!");
        titleLabel.getStyleClass().add("label"); // Apply CSS class to titleLabel
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button signInButton = new Button("SIGN IN");
        signInButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (userAuthenticator.authenticateUser(username, password)) 
            {
                System.out.println("Authentication successful!");
                createQuiz();
            } 
            else
            {
                System.out.println("Authentication failed. Please try again.");
            }
        });
        Button signUpButton = new Button("SIGN UP");
        signUpButton.setOnAction(event -> {
            userAuthenticator.displaySignUpForm(primaryStage);
        });
        VBox root = new VBox(10);
        root.getChildren().addAll(titleLabel, usernameField, passwordField, signInButton, signUpButton);
        Scene scene = new Scene(root, 300, 200);
        applyCSS(scene);
        primaryStage.setScene(scene);
    }    
    public void createQuiz() 
    {
        primaryStage.setTitle("Create Quiz");
        Label questionLabel = new Label("Enter your question:");
        questionLabel.setStyle("-fx-font-weight: bold;");
        TextArea questionTextArea = new TextArea();
        questionTextArea.setWrapText(true);
        Label optionsLabel = new Label("Enter the options (separated by commas):");
        optionsLabel.setStyle("-fx-font-weight: bold;");
        TextField optionsTextField = new TextField();
        Label correctAnswerLabel = new Label("Enter the correct answer:");
        correctAnswerLabel.setStyle("-fx-font-weight: bold;");
        TextField correctAnswerTextField = new TextField();
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-font-weight: bold;");
        nextButton.setOnAction(event -> {
            String question = questionTextArea.getText();
            String[] options = optionsTextField.getText().split(",");
            String correctAnswer = correctAnswerTextField.getText();
            if (question.isEmpty() || options.length < 2 || correctAnswer.isEmpty())
            {
                // Display an error message if any field is empty
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields.");
                alert.showAndWait();
                return;
            }
            Question q = new Question(question, options, correctAnswer);
            questions.add(q);
            // Clear fields for next question
            questionTextArea.clear();
            optionsTextField.clear();
            correctAnswerTextField.clear();
        });
        Button finishButton = new Button("Finish");
        finishButton.setStyle("-fx-font-weight: bold;");
        finishButton.setOnAction(event -> {
            if (questions.isEmpty()) 
            {
                // Display an error message if no questions have been added
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please add at least one question.");
                alert.showAndWait();
                return;
            }
            // Process the questions (for demonstration, just printing them)
            for (Question q : questions) 
            {
                System.out.println("Question: " + q.getQuestion());
                System.out.println("Options:");
                for (String option : q.getOptions()) 
                {
                    System.out.println("- " + option);
                }
                System.out.println("Correct Answer: " + q.getCorrectAnswer());
                System.out.println();
            }
            primaryStage.close();
            // Create a new scene for quiz options (attempt or share)
            Stage optionsStage = new Stage();
            optionsStage.setTitle("Quiz Options");
            HBox optionsLayout = new HBox(50);     
            Button attemptButton = new Button("Attempt Quiz");
            attemptButton.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.8) , 10,0,0,1 );");
            attemptButton.getStyleClass().add("button"); // Apply CSS class to attemptButton
            attemptButton.setOnAction(e -> {
                optionsStage.close();
                takeQuiz();
            });
            Button shareButton = new Button("Share Quiz");
            shareButton.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.8) , 10,0,0,1 );");
            shareButton.setOnAction(e -> {
                optionsStage.close();
                // Logic for sharing the quiz (e.g., copy to clipboard, share via email or messaging app)
                System.out.println("Quiz shared!");
            });
            VBox buttonBox = new VBox(10);
            buttonBox.setAlignment(Pos.CENTER); // Align buttons to the center of the VBox
            buttonBox.getChildren().addAll(attemptButton, shareButton);
            Scene optionsScene = new Scene(buttonBox, 300, 200);
            optionsStage.setScene(optionsScene);
            optionsStage.show();
        });
        HBox buttonBox = new HBox(255);
        buttonBox.getChildren().addAll(nextButton, finishButton);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10)); // Set padding of 10px on all sides
        layout.getChildren().addAll(
                questionLabel, questionTextArea,
                optionsLabel, optionsTextField,
                correctAnswerLabel, correctAnswerTextField,
                buttonBox
        );
        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("resources/styles.css").toExternalForm()); // Load CSS file
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void takeQuiz() 
    {
        int totalQuestions = questions.size(); // Use size() instead of length for lists
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER); // Center the content vertically
        for (int i = 0; i < totalQuestions; i++) 
        {
            Question question = questions.get(i);
            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setAlignment(Pos.CENTER); // Center the question text
            layout.getChildren().add(questionLabel);
            GridPane optionsGrid = new GridPane();
            optionsGrid.setAlignment(Pos.CENTER); // Center the options horizontally
            optionsGrid.setHgap(10); // Add some horizontal spacing between options
            ToggleGroup group = new ToggleGroup();
            for (int j = 0; j < question.getOptions().length; j++)
            {
                String option = question.getOptions()[j];                
                RadioButton radioButton = new RadioButton(option);
                radioButton.setToggleGroup(group);
                optionsGrid.add(radioButton, 0, j);
            }
            layout.getChildren().add(optionsGrid);
            Button submitButton = new Button("Submit");
            submitButton.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;"); // Green color
            int questionIndex = i; // Store the current question index for accessing user's answer
            submitButton.setOnAction(event -> {
                RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                if (selectedRadioButton != null) 
                {
                    String selectedOption = selectedRadioButton.getText();
                    // Store the user's answer in the userAnswers list
                    userAnswers.add(selectedOption);
                    if (selectedOption.equals(question.getCorrectAnswer())) 
                    {
                        score.incrementAndGet(); // Increase the score
                        numCorrectAnswers.incrementAndGet(); // Increase the number of correct answers
                    }
                    // Display if the selected option is correct or not
                    Label resultLabel = new Label("Your answer is " + (selectedOption.equals(question.getCorrectAnswer()) ? "correct!" : "incorrect. The correct answer is " + question.getCorrectAnswer()));
                    resultLabel.setStyle("-fx-font-weight: bold;");
                    layout.getChildren().add(resultLabel);
                    layout.getChildren().removeAll(questionLabel, optionsGrid, submitButton);
                    System.out.println("Your current score: " + score.get());
                }
            });
            layout.getChildren().add(submitButton);
        }
        Button finishQuizButton = new Button("Finish Quiz");
        finishQuizButton.setStyle("-fx-font-weight: bold; -fx-background-color: #f44336; -fx-text-fill: white;"); // Red color
        finishQuizButton.setOnAction(event -> {
            layout.getChildren().clear(); // Clear the layout
            int correctAnswers = 0;
            for (int i = 0; i < totalQuestions; i++) 
            {
                Question question = questions.get(i);
                String userAnswer = userAnswers.get(i);
                Label resultLabel = new Label("Question: " + question.getQuestion() + "\nYour Answer: " + userAnswer);
                resultLabel.setStyle("-fx-font-weight: bold;");
                layout.getChildren().add(resultLabel);
                if (userAnswer.equals(question.getCorrectAnswer())) 
                {
                    correctAnswers++;
                    resultLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } 
                else
                {
                    Label correctAnswerLabel = new Label("Correct Answer: " + question.getCorrectAnswer());
                    correctAnswerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    layout.getChildren().addAll(correctAnswerLabel, new Separator());
                }
            }
            Label scoreLabel = new Label("Your score: " + score.get() + " out of " + totalQuestions);
            Label correctnessLabel = new Label("Number of correct answers: " + correctAnswers);
            scoreLabel.setStyle("-fx-font-weight: bold;");
            correctnessLabel.setStyle("-fx-font-weight: bold;");
            layout.getChildren().addAll(scoreLabel, correctnessLabel);
            Button reattemptButton = new Button("Reattempt Quiz");
            reattemptButton.setStyle("-fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white;"); // Blue color
            reattemptButton.setOnAction(e -> {
                layout.getChildren().clear(); // Clear the layout
                userAnswers.clear(); // Clear the user's answers
                score.set(0); // Reset the score
                numCorrectAnswers.set(0); // Reset the number of correct answers
                takeQuiz(); // Start the quiz again
            });
            Button thankYouButton = new Button("Thank You");
            thankYouButton.setStyle("-fx-font-weight: bold; -fx-background-color: #9E9E9E; -fx-text-fill: white;"); // Grey color
            thankYouButton.setOnAction(e -> {
                // Display a thank you message
                layout.getChildren().clear();
                Label thankYouLabel = new Label("Thank you for attempting the quiz!");
                thankYouLabel.setStyle("-fx-font-weight: bold;");
                layout.getChildren().add(thankYouLabel);
            });
            layout.getChildren().addAll(reattemptButton, thankYouButton);
        });
        layout.getChildren().add(finishQuizButton);
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("resources/styles.css").toExternalForm()); // Load CSS file
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void attemptQuiz() 
    {
        primaryStage.setTitle("Attempt Quiz");
        VBox layout = new VBox(20); // Increased spacing between elements
        layout.setAlignment(Pos.CENTER); // Center the content vertically
        for (Question question : questions) 
        {
            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setAlignment(Pos.CENTER); // Center the question text
            questionLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 0 10px 0;"); // Apply CSS styles inline
            layout.getChildren().add(questionLabel);
            ToggleGroup group = new ToggleGroup();
            for (String option : question.getOptions())
            {
                RadioButton radioButton = new RadioButton(option);
                radioButton.setToggleGroup(group);
                radioButton.setStyle("-fx-padding: 5px 0; -fx-font-size: 14px;"); // Apply CSS styles inline
                layout.getChildren().add(radioButton);
            }
            layout.getChildren().add(new Separator()); // Add spacing between questions
            HBox buttonBox = new HBox(10); // Create an HBox for buttons
            buttonBox.setAlignment(Pos.CENTER); // Center the buttons horizontally
            Button submitButton = new Button("Submit");
            submitButton.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-font-size: 14px;"); // Apply CSS styles inline
            submitButton.setOnAction(event -> {
                RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                if (selectedRadioButton != null && selectedRadioButton.getText().equals(question.getCorrectAnswer())) 
                {
                    score.incrementAndGet();
                }
            });
            Button finishButton = new Button("Finish Quiz");
            finishButton.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-font-size: 14px;"); // Apply CSS styles inline
            finishButton.setOnAction(event -> {
                System.out.println("Quiz completed! Your final score is: " + score.get() + " out of " + questions.size());
                primaryStage.setScene(optionsScene); // Go back to the options scene
            });
            buttonBox.getChildren().addAll(submitButton, finishButton);
            layout.getChildren().add(buttonBox);
        }
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
    }
    public void showResult()
    {
        Stage resultStage = new Stage();
        resultStage.setTitle("Quiz Result");
        Label scoreLabel = new Label("Your final score is: " + score.get() + " out of " + questions.size());
        scoreLabel.getStyleClass().add("result-label"); // Apply CSS class to scoreLabel
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> resultStage.close());
        closeButton.getStyleClass().add("close-button"); // Apply CSS class to closeButton
        VBox layout = new VBox(10);
        layout.getStyleClass().add("result-layout"); // Apply CSS class to layout
        layout.setAlignment(Pos.CENTER); // Center the content vertically
        layout.getChildren().addAll(scoreLabel, closeButton);
        Scene scene = new Scene(layout, 300, 200);
        String cssPath = getClass().getResource("resources/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath); // Load CSS file
        resultStage.setScene(scene);
        resultStage.show();
    }
}