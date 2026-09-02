import React from 'react';

const SkeletonLoader = ({ count = 1, className = '', type = 'text' }) => {
  const skeletons = Array(count).fill(0).map((_, i) => (
    <div 
      key={i} 
      className={`bg-[#161b22] border border-[#30363d] rounded-lg animate-pulse ${
        type === 'text' ? 'h-4 w-full mb-2' : 
        type === 'card' ? 'h-48 w-full' : 
        type === 'avatar' ? 'h-12 w-12 rounded-full' : className
      } ${className}`}
    />
  ));

  return <>{skeletons}</>;
};

export default SkeletonLoader;
