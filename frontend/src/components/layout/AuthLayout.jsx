import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { Brain } from 'lucide-react';

const AuthLayout = () => {
  return (
    <div className="min-h-screen bg-[#0f1117] flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <Link to="/" className="flex items-center text-brand-500 hover:text-brand-400">
            <Brain className="w-10 h-10 mr-2" />
            <span className="text-3xl font-bold text-white tracking-tight">Mock<span className="text-brand-500">IQ</span></span>
          </Link>
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
          Welcome back
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-[#161b22] py-8 px-4 shadow sm:rounded-lg sm:px-10 border border-[#30363d]">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
