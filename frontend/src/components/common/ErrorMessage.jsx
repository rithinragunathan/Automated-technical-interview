import React from 'react';
import { AlertCircle } from 'lucide-react';

const ErrorMessage = ({ message, onRetry }) => {
  return (
    <div className="bg-[#161b22] border border-red-900/50 rounded-lg p-6 flex flex-col items-center text-center">
      <AlertCircle className="w-10 h-10 text-red-500 mb-4" />
      <h3 className="text-lg font-medium text-white mb-2">Something went wrong</h3>
      <p className="text-gray-400 mb-6">{message || 'An unexpected error occurred. Please try again.'}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="px-4 py-2 bg-[#30363d] hover:bg-[#484f58] text-white rounded-md transition-colors"
        >
          Try Again
        </button>
      )}
    </div>
  );
};

export default ErrorMessage;
