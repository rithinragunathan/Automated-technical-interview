/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        mono: ['JetBrains Mono', 'Fira Code', 'Cascadia Code', 'monospace'],
        sans: ['Inter', 'sans-serif'],
      },
      colors: {
        brand: {
          50: '#f2f6f4',
          100: '#e1ebe6',
          200: '#c4d7ce',
          300: '#9db9ad',
          400: '#5d7e71',
          500: '#52786e',
          600: '#3f695f',
          700: '#34564e',
          800: '#29473f',
          900: '#203a34',
        }
      }
    }
  },
  plugins: [],
}
