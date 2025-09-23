class Question {
    constructor(id, text, options, correctAnswer, difficulty, objectiveId, createdBy) {
      this.id = id;
      this.text = text;
      this.options = options;  // array
      this.correctAnswer = correctAnswer;
      this.difficulty = difficulty; // easy | medium | hard
      this.objectiveId = objectiveId;
      this.createdBy = createdBy;   // instructor ID
    }
  }
  
  module.exports = Question;
  