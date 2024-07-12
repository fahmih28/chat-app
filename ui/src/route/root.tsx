import {createHashRouter, RouterProvider} from "react-router-dom";
import RootPage from "./root.page.tsx";

export default function Root() {
    const routers = createHashRouter([{
        path: '/',
        element: <RootPage/>
    },
        {
            path: '/home',
            element: <h2>This is home</h2>
        }
    ]);

    return <RouterProvider router={routers}/>
}
