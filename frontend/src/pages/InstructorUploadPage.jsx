import { useState } from "react";
import api from "../services/api";

export default function InstructorUploadPage() {
  const [text, setText] = useState("");
  const [options, setOptions] = useState(["", "", "", ""]);
  const [correctAnswer, setCorrectAnswer] = useState("");
  const [difficulty, setDifficulty] = useState("easy");
  const [objectiveId, setObjectiveId] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.post("/questions", {
      text,
      options,
      correctAnswer,
      difficulty,
      objectiveId
    });
    alert("Question uploaded successfully!");
  };

  return (
    <form onSubmit={handleSubmit} className="upload-form">
      <h2>Upload New Question</h2>

      <label>Question Text:</label>
      <textarea value={text} onChange={(e) => setText(e.target.value)} required />

      <label>Options:</label>
      {options.map((opt, i) => (
        <input
          key={i}
          type="text"
          placeholder={`Option ${i + 1}`}
          value={opt}
          onChange={(e) => {
            const newOptions = [...options];
            newOptions[i] = e.target.value;
            setOptions(newOptions);
          }}
          required
        />
      ))}

      <label>Correct Answer:</label>
      <input
        type="text"
        value={correctAnswer}
        onChange={(e) => setCorrectAnswer(e.target.value)}
        required
      />

      <label>Difficulty:</label>
      <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
        <option value="easy">Easy</option>
        <option value="medium">Medium</option>
        <option value="hard">Hard</option>
      </select>

      <label>Learning Objective ID:</label>
      <input
        type="text"
        value={objectiveId}
        onChange={(e) => setObjectiveId(e.target.value)}
        required
      />

      <button type="submit">Upload Question</button>
    </form>
  );
}
