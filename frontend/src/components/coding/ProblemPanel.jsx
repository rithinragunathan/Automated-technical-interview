import React from 'react';
import Badge from '../common/Badge';
import { getDifficultyColor } from '../../utils/scoreHelpers';

const ProblemPanel = ({ problem }) => {
  if (!problem) return null;

  return (
    <div className="h-full bg-[#161b22] border-r border-[#30363d] overflow-y-auto custom-scrollbar">
      <div className="p-6">
        <div className="flex items-start justify-between mb-4">
          <h1 className="text-2xl font-bold text-white">{problem.title}</h1>
          <Badge 
            variant={getDifficultyColor(problem.difficulty)} 
            text={problem.difficulty} 
          />
        </div>
        
        <div className="flex flex-wrap gap-2 mb-6">
          <Badge variant="neutral" text={problem.topic} />
          {problem.tags && problem.tags.map((tag, idx) => (
            <Badge key={idx} variant="neutral" text={tag} />
          ))}
        </div>

        <div className="prose prose-invert max-w-none text-gray-300 text-sm leading-relaxed mb-8">
          {/* In a real app, render Markdown here */}
          <div className="whitespace-pre-wrap">{problem.description}</div>
        </div>

        {problem.examples && problem.examples.length > 0 && (
          <div className="space-y-6 mb-8">
            <h3 className="text-lg font-semibold text-white border-b border-[#30363d] pb-2">Examples</h3>
            {problem.examples.map((example, idx) => (
              <div key={idx} className="bg-[#0f1117] rounded-md p-4 border border-[#30363d]">
                <div className="mb-2">
                  <span className="font-semibold text-gray-400">Input: </span>
                  <span className="font-mono text-sm text-gray-200">{example.input}</span>
                </div>
                <div className="mb-2">
                  <span className="font-semibold text-gray-400">Output: </span>
                  <span className="font-mono text-sm text-green-400">{example.output}</span>
                </div>
                {example.explanation && (
                  <div>
                    <span className="font-semibold text-gray-400">Explanation: </span>
                    <span className="text-sm text-gray-300">{example.explanation}</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {problem.constraints && problem.constraints.length > 0 && (
          <div>
            <h3 className="text-lg font-semibold text-white border-b border-[#30363d] pb-2 mb-4">Constraints</h3>
            <ul className="list-disc pl-5 space-y-2 text-sm text-gray-300 font-mono">
              {problem.constraints.map((constraint, idx) => (
                <li key={idx}>{constraint}</li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProblemPanel;
