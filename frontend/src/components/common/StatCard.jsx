import React from 'react';
import { ArrowUpRight, ArrowDownRight } from 'lucide-react';

const StatCard = ({ title, value, icon: Icon, trend, trendValue }) => {
  const isPositive = trend === 'up';
  
  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5 flex flex-col h-full">
      <div className="flex justify-between items-start mb-4">
        <div className="p-2 bg-[#0f1117] rounded-lg border border-[#30363d]">
          {Icon && <Icon className="w-5 h-5 text-gray-400" />}
        </div>
        {trend && (
          <div className={`flex items-center text-xs font-medium px-2 py-1 rounded-full ${
            isPositive ? 'text-green-400 bg-green-900/20' : 'text-red-400 bg-red-900/20'
          }`}>
            {isPositive ? <ArrowUpRight className="w-3 h-3 mr-1" /> : <ArrowDownRight className="w-3 h-3 mr-1" />}
            {trendValue}
          </div>
        )}
      </div>
      <div>
        <h4 className="text-gray-400 text-sm font-medium mb-1">{title}</h4>
        <div className="text-2xl font-bold text-white">{value}</div>
      </div>
    </div>
  );
};

export default StatCard;
