from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

# Initialize FastAPI app
app = FastAPI()

# Load a summarization model
summarizer = pipeline("summarization", model="facebook/bart-large-cnn")

# Define the request structure
class ReadingInput(BaseModel):
    text: str

@app.get("/")
def home():
    return {"message": "ML service is running!"}

@app.post("/generate-objectives")
def generate_objectives(data: ReadingInput):
    # Step 1: Summarize the reading
    summary = summarizer(data.text, max_length=150, min_length=60, do_sample=False)[0]['summary_text']

    # Step 2: Create learning objectives (simple example prompt)
    # You can replace this with a GPT model or a better template later
    objectives = [
        f"Understand the main idea of: {summary.split('.')[0]}",
        f"Analyze key factors related to: {summary.split('.')[1] if len(summary.split('.')) > 1 else summary}",
        f"Apply knowledge from this reading to real-world examples."
    ]

    return {"summary": summary, "objectives": objectives}
