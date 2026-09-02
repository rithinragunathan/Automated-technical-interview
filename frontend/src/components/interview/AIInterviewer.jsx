import React, { useState, useEffect, useRef } from 'react';
import { Brain, User, Send, Loader2 } from 'lucide-react';

const AIInterviewer = ({ currentQuestion, messages, onSendMessage, isProcessing }) => {
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isProcessing]);

  const handleSend = () => {
    if (inputText.trim() && !isProcessing) {
      onSendMessage(inputText);
      setInputText('');
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="flex flex-col h-full bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
      <div className="p-4 border-b border-[#30363d] bg-[#0f1117] flex items-center">
        <div className="w-10 h-10 rounded-full bg-brand-900/50 flex items-center justify-center border border-brand-500/30 mr-3">
          <Brain className="w-6 h-6 text-brand-400" />
        </div>
        <div>
          <h2 className="text-lg font-semibold text-white">AI Interviewer</h2>
          <p className="text-xs text-brand-400">MockIQ Assistant</p>
        </div>
      </div>

      <div className="flex-grow p-4 overflow-y-auto space-y-4">
        {messages.map((msg, index) => (
          <div key={index} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`flex max-w-[80%] ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
              <div className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center ${
                msg.role === 'user' ? 'bg-[#30363d] ml-3' : 'bg-brand-900/50 border border-brand-500/30 mr-3'
              }`}>
                {msg.role === 'user' ? <User className="w-4 h-4 text-gray-300" /> : <Brain className="w-4 h-4 text-brand-400" />}
              </div>
              <div className={`px-4 py-3 rounded-lg ${
                msg.role === 'user' 
                  ? 'bg-[#30363d] text-white rounded-tr-none' 
                  : 'bg-brand-900/20 border border-brand-500/20 text-gray-200 rounded-tl-none'
              }`}>
                <p className="whitespace-pre-wrap text-sm leading-relaxed">{msg.content}</p>
                {msg.feedback && (
                  <div className="mt-3 pt-3 border-t border-brand-500/20 text-xs text-brand-300">
                    <strong>Feedback:</strong> {msg.feedback}
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
        {isProcessing && (
          <div className="flex justify-start">
            <div className="flex max-w-[80%] flex-row">
              <div className="flex-shrink-0 w-8 h-8 rounded-full bg-brand-900/50 border border-brand-500/30 mr-3 flex items-center justify-center">
                <Brain className="w-4 h-4 text-brand-400" />
              </div>
              <div className="px-4 py-3 rounded-lg bg-brand-900/20 border border-brand-500/20 text-gray-200 rounded-tl-none flex items-center">
                <Loader2 className="w-4 h-4 animate-spin text-brand-400 mr-2" />
                <span className="text-sm">AI is thinking...</span>
              </div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <div className="p-4 border-t border-[#30363d] bg-[#0f1117]">
        <div className="relative">
          <textarea
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Type your response... (Press Enter to send, Shift+Enter for new line)"
            className="w-full bg-[#161b22] border border-[#30363d] rounded-lg pl-4 pr-12 py-3 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-brand-500 resize-none"
            rows={3}
            disabled={isProcessing}
          />
          <button
            onClick={handleSend}
            disabled={!inputText.trim() || isProcessing}
            className="absolute right-2 bottom-2 p-2 rounded-md bg-brand-600 text-white disabled:opacity-50 hover:bg-brand-500 transition-colors"
          >
            <Send className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default AIInterviewer;
