# Cookbook Database Schema Explanation

## Users Table
Stores information about users who have accounts in the system. Each user has a unique identifier (`user_id`), a username, a hashed password, a display name, and an admin flag indicating whether they have administrative privileges.

## Recipes Table
Contains details about various recipes. Each recipe has a unique identifier (`recipe_id`), a name, a short description, a detailed description, the number of people the recipe serves (`person_count`), the ID of the user who created the recipe (`user_id`), and timestamps for creation and last update.

## Ingredients Table
Stores a list of ingredients used in recipes. Each ingredient has a unique identifier (`ingredient_id`) and a name.

## RecipeIngredients Table
This table establishes a many-to-many relationship between recipes and ingredients, allowing a recipe to have multiple ingredients and an ingredient to be used in multiple recipes. It includes the `recipe_id` and `ingredient_id` as foreign keys along with the quantity and unit of each ingredient required for the recipe.

## Tags Table
Stores tags that can be associated with recipes. Each tag has a unique identifier (`tag_id`) and a name.

## RecipeTags Table
Establishes a many-to-many relationship between recipes and tags, allowing multiple tags to be assigned to a recipe and a tag to be associated with multiple recipes.

## Comments Table
Stores comments made by users on recipes. Each comment has a unique identifier (`comment_id`), the ID of the recipe it relates to (`recipe_id`), the ID of the user who made the comment (`user_id`), the comment text, and a timestamp for when the comment was made.

## Weeks Table
Represents weeks, typically used for meal planning. Each week has a unique identifier (`week_id`), a start date, and an end date.

## WeekRecipes Table
Associates recipes with specific weeks, specifying which recipe is planned for which day of the week and for what meal type (e.g., breakfast, lunch, dinner).

## ShoppingList Table
Used for generating shopping lists based on planned meals for a particular week. It includes a unique identifier (`list_id`), the ID of the week it corresponds to (`week_id`), the ID of the ingredient (`ingredient_id`), the quantity of the ingredient needed, and the unit of measurement.

# MySQL code:
```sql
CREATE DATABASE IF NOT EXISTS cookbook;
USE cookbook;

-- Users Table
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    admin_flag BOOLEAN DEFAULT FALSE
);

-- Recipes Table
CREATE TABLE Recipes (
    recipe_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    short_description TEXT,
    detailed_description TEXT,
    person_count INT,
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Ingredients Table
CREATE TABLE Ingredients (
    ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- RecipeIngredients Table
CREATE TABLE RecipeIngredients (
    recipe_id INT,
    ingredient_id INT,
    quantity DECIMAL(10, 2),
    unit VARCHAR(50),
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id)
);

-- Tags Table
CREATE TABLE Tags (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- RecipeTags Table
CREATE TABLE RecipeTags (
    recipe_id INT,
    tag_id INT,
    PRIMARY KEY (recipe_id, tag_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id),
    FOREIGN KEY (tag_id) REFERENCES Tags(tag_id)
);

-- Comments Table
CREATE TABLE Comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id INT,
    user_id INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Weeks Table
CREATE TABLE Weeks (
    week_id INT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE,
    end_date DATE
);

-- WeekRecipes Table
CREATE TABLE WeekRecipes (
    week_id INT,
    recipe_id INT,
    day_of_week INT,
    meal_type VARCHAR(50),
    PRIMARY KEY (week_id, recipe_id),
    FOREIGN KEY (week_id) REFERENCES Weeks(week_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id)
);

-- ShoppingList Table
CREATE TABLE ShoppingList (
    list_id INT AUTO_INCREMENT PRIMARY KEY,
    week_id INT,
    ingredient_id INT,
    quantity DECIMAL(10, 2),
    unit VARCHAR(50),
    FOREIGN KEY (week_id) REFERENCES Weeks(week_id),
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id)
);
