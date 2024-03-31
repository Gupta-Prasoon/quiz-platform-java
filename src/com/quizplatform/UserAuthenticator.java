package com.quizplatform;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
public class UserAuthenticator 
{
    private Stage primaryStage;
    private List<User> users = new ArrayList<>();
    private List<Question> questions;
    private User currentUser; // Track the current user attempting the quiz
    private VBox optionsLayout;
    private AtomicInteger score = new AtomicInteger(0);  // Declare score as a class-level field
    private AtomicInteger numCorrectAnswers = new AtomicInteger(0);
    private List<String> userAnswers = new ArrayList<>(); // Store user's answers
    public UserAuthenticator(List<Question> questions)
    {
        this.questions = questions;
        // Add some default users
        users.add(new User("admin", "admin123"));
    }
    public UserAuthenticator() 
    {
        this.questions = new ArrayList<>();
        // Add some default users
        users.add(new User("admin", "admin123"));
    }
    public boolean authenticateUser(String username, String password)
    {
        for (User user : users)
        {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) 
            {
                currentUser = user; // Set the current user
                return true;
            }
        }
        return false;
    }
    public void displaySignUpForm(Stage primaryStage) 
    {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sign up");
        Label signUpLabel = new Label("Sign up for a new account:");
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("New Username");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(event -> {
            String newUsername = newUsernameField.getText();
            String newPassword = newPasswordField.getText();
            users.add(new User(newUsername, newPassword));
            System.out.println("Account created successfully!");
            currentUser = users.get(users.size() - 1); // Set the current user
            primaryStage.close();
            // Open a new window for quiz creation
            Stage quizCreationStage = new Stage();
            createQuizPlatform(quizCreationStage);
        });
        VBox signUpLayout = new VBox(10);
        signUpLayout.getChildren().addAll(signUpLabel, newUsernameField, newPasswordField, createAccountButton);
        Scene signUpScene = new Scene(signUpLayout, 300, 200);
        signUpScene.getStylesheets().add(getClass().getResource("resources/styles.css").toExternalForm()); // Load CSS file
        primaryStage.setScene(signUpScene);
        primaryStage.show();
    }
    public void createQuizPlatform(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Create Quiz");
        Label questionLabel = new Label("Enter your question:");
        TextArea questionTextArea = new TextArea();
        questionTextArea.setWrapText(true);
        Label answerLabel = new Label("Enter the options (separated by commas):");
        TextField optionsTextField = new TextField();
        Label correctAnswerLabel = new Label("Enter the correct answer:");
        TextField correctAnswerTextField = new TextField();
        Button nextButton = new Button("Next");
        nextButton.setOnAction(event -> {
            String question = questionTextArea.getText();
            String[] options = optionsTextField.getText().split(",");
            String correctAnswer = correctAnswerTextField.getText();
            Question q = new Question(question, options, correctAnswer);
            questions.add(q);
            questionTextArea.clear();
            optionsTextField.clear();
            correctAnswerTextField.clear();
        });
        Button finishButton = new Button("Finish");
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
            VBox optionsLayout = new VBox(10); // Initialize optionsLayout
            Button attemptButton = new Button("Attempt Quiz");
            attemptButton.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.8) , 10,0,0,1 );");
            attemptButton.setOnAction(e -> {
                optionsStage.close(); // Close the options window
                takeQuiz(); // Take the quiz
            });
            Button shareButton = new Button("Share Quiz");
            shareButton.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.8) , 10,0,0,1 );");
            shareButton.setOnAction(e -> {
                optionsStage.close(); // Close the options window
                // Logic for sharing the quiz (e.g., copy to clipboard, share via email or messaging app)
                System.out.println("Quiz shared!");
            });
            VBox buttonBox = new VBox(10);
            buttonBox.setAlignment(Pos.CENTER); // Align buttons to the center of the VBox
            optionsLayout.getChildren().addAll(attemptButton, shareButton);
            Scene optionsScene = new Scene(optionsLayout, 300, 200);
            optionsStage.setScene(optionsScene);
            optionsStage.show();
        });       
        HBox buttonBox = new HBox(255);
        buttonBox.getChildren().addAll(nextButton, finishButton);
        VBox layout = new VBox(10);
        layout.getChildren().addAll(questionLabel, questionTextArea, answerLabel, optionsTextField, correctAnswerLabel, correctAnswerTextField, nextButton, finishButton);
        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("resources/styles.css").toExternalForm()); // Load CSS file
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void takeQuiz() 
    {
        int totalQuestions = questions.size();
        VBox layout = new VBox(10);
        for (int i = 0; i < totalQuestions; i++)
        {
            Question question = questions.get(i);
            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setStyle("-fx-font-weight: bold;"); // Make the question bold
            layout.getChildren().add(questionLabel);
            ToggleGroup group = new ToggleGroup();
            for (String option : question.getOptions())
            {
                RadioButton radioButton = new RadioButton(option);
                radioButton.setToggleGroup(group);
                layout.getChildren().add(radioButton);
            }
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
                    resultLabel.setStyle("-fx-font-weight: bold;"); // Make the result bold
                    layout.getChildren().add(resultLabel);
                    layout.getChildren().removeAll(questionLabel);
                    layout.getChildren().removeAll(group.getToggles());
                    layout.getChildren().removeAll(submitButton);
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
                resultLabel.setStyle("-fx-font-weight: bold;"); // Make the result bold
                layout.getChildren().add(resultLabel);
                if (userAnswer.equals(question.getCorrectAnswer())) 
                {
                    correctAnswers++;
                    resultLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); // Make the correct answer green and bold
                }
                else
                {
                    Label correctAnswerLabel = new Label("Correct Answer: " + question.getCorrectAnswer());
                    correctAnswerLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); // Make the correct answer green and bold
                    layout.getChildren().add(correctAnswerLabel);
                    resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Make the user's answer red and bold
                }
                layout.getChildren().add(new Separator());
            }
            Label scoreLabel = new Label("Your score: " + score.get() + " out of " + totalQuestions);
            Label correctnessLabel = new Label("Number of correct answers: " + correctAnswers);
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
                thankYouLabel.setStyle("-fx-font-weight: bold;"); // Make the thank you message bold
                layout.getChildren().add(thankYouLabel);
            });
            layout.getChildren().addAll(reattemptButton, thankYouButton);
        });
        layout.getChildren().add(finishQuizButton);
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("resources/styles.css").toExternalForm()); // Load the CSS file
        primaryStage.setScene(scene);
        primaryStage.show();
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
        layout.getChildren().addAll(scoreLabel, closeButton);
        Scene scene = new Scene(layout, 300, 200);
        String cssPath = getClass().getResource("resources/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath); // Load CSS file
        resultStage.setScene(scene);
        resultStage.show();
    }
}