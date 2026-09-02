import React from 'react';
import { Loader2 } from 'lucide-react';

const LoadingSpinner = ({ fullScreen, message = 'Loading...' }) => {
  const content = (
    <div className="flex flex-col items-center justify-center space-y-4">
      <Loader2 className="w-10 h-10 text-brand-500 animate-spin" />
      {message && <p className="text-gray-400 text-sm font-medium">{message}</p>}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-[#0f1117] flex items-center justify-center z-50">
        {content}
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center p-8 w-full h-full min-h-[200px]">
      {content}
    </div>
  );
};

export default LoadingSpinner;
