import os
import requests
import pandas as pd
import tensorflow as tf
from sklearn.preprocessing import MultiLabelBinarizer
from flask import Flask, request, jsonify
from google.cloud import storage

# Initialize Flask
app = Flask(__name__)

# Google Cloud Storage configuration
bucket_name = 'model-bucket-mona'
model_blob_name = 'recommendation_model.h5'
dataset_blob_name = 'dataset_food.csv'

# Function to download file from GCS
def download_from_gcs(bucket_name, source_blob_name, destination_file_name):
    storage_client = storage.Client()
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(source_blob_name)
    blob.download_to_filename(destination_file_name)
    print(f'File {source_blob_name} downloaded to {destination_file_name}.')

# Define local paths
model_local_path = 'recommendation_model.h5'
dataset_local_path = 'dataset_food.csv'

# Download the model and dataset if they do not exist locally
if not os.path.exists(model_local_path):
    download_from_gcs(bucket_name, model_blob_name, model_local_path)
if not os.path.exists(dataset_local_path):
    download_from_gcs(bucket_name, dataset_blob_name, dataset_local_path)

# Load the model using TensorFlow
model = tf.keras.models.load_model(model_local_path, compile=False)

# Function to fetch user data from external API
def get_user_data(credentials):
    api_url = f"http://35.240.207.177:8080/mona/users/{credentials}"
    response = requests.get(api_url)
    
    if response.status_code != 200:
        return None

    data = response.json()
    return data

# Function to fetch user history from external API
def get_user_history(credentials):
    api_url = f"http://35.240.207.177:8080/mona/transactions/{credentials}"
    response = requests.get(api_url)
    
    if response.status_code != 200:
        return None

    data = response.json()
    user_history = pd.DataFrame(data)
    return user_history

# Endpoint to get recommendations
@app.route('/recommend', methods=['GET'])
def recommend():
    credentials = request.args.get('credentials')
    if not credentials:
        return jsonify({'error': 'Credentials are required'}), 400
    
    # Fetch user data from API
    user_data = get_user_data(credentials)
    if user_data is None:
        return jsonify({'error': 'No user data found for the given credentials'}), 404
    
    user_name = user_data.get('nama', 'User')
    
    # Fetch user history from API
    user_history = get_user_history(credentials)
    if user_history is None or user_history.empty:
        return jsonify({'error': 'No history found for the given credentials'}), 404

    # Preprocess Dataset (adjust as per your dataset)
    dfdataset = pd.read_csv(dataset_local_path)
    dataset = dfdataset.copy()
    dataset = dataset.drop(['price_category', 'price_min', 'price_max', 'lat', 'lng', 'link'], axis=1)
    dataset = dataset.reset_index(drop=True)
    dataset['category'] = dataset['category'].apply(lambda x: x.split('/'))

    mlb = MultiLabelBinarizer()
    dataset_cats = mlb.fit_transform(dataset['category'])
    dataset_cat_df = pd.DataFrame(dataset_cats, columns=mlb.classes_, index=dataset['merchant_name'])

    # Preprocess User History
    history = user_history.copy()
    history = history.drop(["category", "amount", "action"], axis=1)

    user_cats = mlb.transform(history['food_category'].apply(lambda x: [x]))
    user_cat_df = pd.DataFrame(user_cats, columns=mlb.classes_)
    user_profile = user_cat_df.sum(axis=0)

    # Ensure the user profile has the same features as the dataset
    if len(user_profile) != len(dataset_cat_df.columns):
        missing_cols = set(dataset_cat_df.columns) - set(user_profile.index)
        for col in missing_cols:
            user_profile[col] = 0
        user_profile = user_profile[dataset_cat_df.columns]

    # Convert to tensors
    dataset_tensor = tf.convert_to_tensor(dataset_cat_df.values, dtype=tf.float32)
    user_profile_tensor = tf.convert_to_tensor(user_profile.values, dtype=tf.float32)
    user_profile_tensor = tf.expand_dims(user_profile_tensor, axis=0)
    user_profile_tensor = tf.tile(user_profile_tensor, [dataset_tensor.shape[0], 1])

    # Predict
    similarity_scores = model([dataset_tensor, user_profile_tensor])

    # Create a DataFrame for similarity scores
    similarity_df = pd.DataFrame(similarity_scores.numpy(), index=dataset['merchant_name'], columns=['similarity'])

    # Combine with original dataset to get additional details
    recommendations = similarity_df.merge(dfdataset[['merchant_name', 'category', 'target_price', 'price_category', 'price_min', 'price_max', 'alamat', 'lat', 'lng', 'link']], left_index=True, right_on='merchant_name')

    # Sort merchants by similarity score
    recommendations = recommendations.sort_values(by='similarity', ascending=False).head(10)

    # Drop the similarity column
    recommendations = recommendations.drop(columns=['similarity'])

    # Convert recommendations to JSON format
    recommendations_json = recommendations.to_dict(orient='records')

    return jsonify({'recommendations': recommendations_json})

# Run the API
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
