import { Route, Routes } from 'react-router-dom';
import { DefaultLayout } from './components/layout/DefaultLayout';
import { RegisterPage } from './pages/RegisterPage';

function App(): React.JSX.Element {
  return (
    <Routes>
      <Route path="/" element={<DefaultLayout />}>
        <Route path="/register" element={<RegisterPage />} />
      </Route>
    </Routes>
  );
}

export default App;