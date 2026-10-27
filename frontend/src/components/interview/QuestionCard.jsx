import React, { useState } from 'react';
import { Mic, MicOff, Send } from 'lucide-react';
import useSpeechRecognition from '../../hooks/useSpeechRecognition';

const QuestionCard = ({ question, onSubmitAnswer, isSubmitting }) => {
  const [answerText, setAnswerText] = useState('');
  const { error: voiceError, isListening, isSupported, start, stop } =
    useSpeechRecognition(answerText, setAnswerText);

  const handleSubmit = async () => {
    if (answerText.trim() && !isSubmitting) {
      stop();
      const submitted = await onSubmitAnswer(answerText);
      if (submitted) setAnswerText('');
    }
  };

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden flex flex-col h-full">
      <div className="p-5 border-b border-[#30363d] bg-[#0f1117]/50">
        {question.questionType === 'AI_GENERATED' && (
          <p className="text-xs font-medium text-brand-400 mb-2">Gemini-generated question</p>
        )}
        <h3 className="text-lg font-medium text-white">{question.text}</h3>
      </div>
      
      <div className="p-5 flex-grow flex flex-col">
        <div className="flex items-center justify-between mb-2">
          <label htmlFor="answer" className="block text-sm font-medium text-gray-400">
            Your Answer
          </label>
          <button
            type="button"
            onClick={isListening ? stop : start}
            disabled={!isSupported || isSubmitting}
            aria-label={isListening ? 'Stop voice input' : 'Start voice input'}
            aria-pressed={isListening}
            className={`inline-flex items-center gap-2 rounded-md border px-3 py-2 text-sm transition-colors disabled:cursor-not-allowed disabled:opacity-50 ${
              isListening
                ? 'border-red-500/60 bg-red-900/30 text-red-300'
                : 'border-[#484f58] bg-[#161b22] text-gray-200 hover:border-brand-500'
            }`}
          >
            {isListening ? <MicOff className="h-4 w-4" /> : <Mic className="h-4 w-4" />}
            {isListening ? 'Stop listening' : 'Answer by voice'}
          </button>
        </div>
        <textarea
          id="answer"
          rows={6}
          className="w-full flex-grow bg-[#0f1117] border border-[#30363d] rounded-md p-3 text-white placeholder-gray-600 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500 font-mono text-sm resize-none"
          placeholder={isListening ? 'Listening... your speech will appear here.' : 'Type your answer or use voice input...'}
          value={answerText}
          onChange={(e) => setAnswerText(e.target.value)}
          disabled={isSubmitting}
        />
        <p className="mt-2 text-xs text-gray-500">
          Voice input uses your browser&apos;s speech recognition. Review the transcript before submitting.
        </p>
        {isListening && (
          <p className="mt-2 text-xs text-brand-300">Stop listening to review your transcript before submitting.</p>
        )}
        {!isSupported && (
          <p className="mt-2 text-xs text-yellow-400">
            Voice input is not supported in this browser. You can type your answer instead.
          </p>
        )}
        {voiceError && <p role="alert" className="mt-2 text-xs text-red-400">{voiceError}</p>}

        <div className="mt-4 flex justify-end">
          <button
            onClick={handleSubmit}
            disabled={!answerText.trim() || isSubmitting || isListening}
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
