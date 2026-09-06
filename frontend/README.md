# Visitor FastPass Frontend

React/Vite UI for the Visitor FastPass microservice backend.

## Run

```powershell
npm install
npm run dev
```

The UI runs on `http://localhost:5173` and calls the Gateway at `http://localhost:8080`.

To change the Gateway URL, copy `.env.example` to `.env` and update `VITE_API_BASE_URL`.
