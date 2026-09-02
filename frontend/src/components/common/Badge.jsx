import React from 'react';

const Badge = ({ variant = 'neutral', text, className = '' }) => {
  const styles = {
    success: 'bg-green-900/30 text-green-400 border border-green-900/50',
    error: 'bg-red-900/30 text-red-400 border border-red-900/50',
    warning: 'bg-yellow-900/30 text-yellow-400 border border-yellow-900/50',
    info: 'bg-blue-900/30 text-blue-400 border border-blue-900/50',
    brand: 'bg-brand-900/30 text-brand-400 border border-brand-900/50',
    neutral: 'bg-[#30363d] text-gray-300 border border-[#484f58]',
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${styles[variant]} ${className}`}>
      {text}
    </span>
  );
};

export default Badge;
