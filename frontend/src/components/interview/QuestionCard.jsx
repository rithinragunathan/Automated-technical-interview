import React, { useState } from 'react';
import { Send } from 'lucide-react';

const QuestionCard = ({ question, onSubmitAnswer, isSubmitting }) => {
  const [answerText, setAnswerText] = useState('');

  const handleSubmit = () => {
    if (answerText.trim() && !isSubmitting) {
      onSubmitAnswer(question.id, answerText);
      setAnswerText('');
    }
  };

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden flex flex-col h-full">
      <div className="p-5 border-b border-[#30363d] bg-[#0f1117]/50">
        <h3 className="text-lg font-medium text-white">{question.text}</h3>
      </div>
      
      <div className="p-5 flex-grow flex flex-col">
        <label htmlFor="answer" className="block text-sm font-medium text-gray-400 mb-2">
          Your Answer
        </label>
        <textarea
          id="answer"
          rows={6}
          className="w-full flex-grow bg-[#0f1117] border border-[#30363d] rounded-md p-3 text-white placeholder-gray-600 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500 font-mono text-sm resize-none"
          placeholder="Type your answer here..."
          value={answerText}
          onChange={(e) => setAnswerText(e.target.value)}
          disabled={isSubmitting}
        />
        
        <div className="mt-4 flex justify-end">
          <button
            onClick={handleSubmit}
            disabled={!answerText.trim() || isSubmitting}
            className="flex items-center px-4 py-2 bg-brand-600 hover:bg-brand-500 disabled:bg-[#30363d] disabled:text-gray-500 text-white rounded-md transition-colors"
          >
            {isSubmitting ? 'Submitting...' : 'Submit Answer'}
            {!isSubmitting && <Send className="w-4 h-4 ml-2" />}
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuestionCard;
