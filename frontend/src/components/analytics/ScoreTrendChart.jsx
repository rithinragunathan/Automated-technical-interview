import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { formatDate } from '../../utils/formatDate';

const ScoreTrendChart = ({ data }) => {
  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={data}
          margin={{ top: 5, right: 20, left: -20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#30363d" vertical={false} />
          <XAxis 
            dataKey="date" 
            tickFormatter={(tick) => formatDate(tick, 'MMM d')}
            stroke="#8b949e" 
            tick={{ fill: '#8b949e', fontSize: 12 }}
            tickLine={false}
            axisLine={{ stroke: '#30363d' }}
          />
          <YAxis 
            domain={[0, 100]} 
            stroke="#8b949e" 
            tick={{ fill: '#8b949e', fontSize: 12 }}
            tickLine={false}
            axisLine={false}
          />
          <Tooltip 
            contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#e6edf3', borderRadius: '8px' }}
            labelFormatter={(label) => formatDate(label)}
          />
          <Legend wrapperStyle={{ paddingTop: '10px' }} />
          <Line 
            type="monotone" 
            name="Overall Score"
            dataKey="score" 
            stroke="#4c6ef5" 
            strokeWidth={3}
            activeDot={{ r: 6, fill: '#4c6ef5', stroke: '#0f1117', strokeWidth: 2 }}
            dot={{ r: 4, fill: '#161b22', stroke: '#4c6ef5', strokeWidth: 2 }}
          />
          <Line 
            type="monotone" 
            name="Coding Score"
            dataKey="codingScore" 
            stroke="#2ea043" 
            strokeWidth={2}
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ScoreTrendChart;
