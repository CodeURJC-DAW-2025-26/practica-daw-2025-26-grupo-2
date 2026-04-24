import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  route("login", "routes/login.tsx"),
  route("register", "routes/user-new.tsx"),
  route("users/:id/edit", "routes/user-edit.tsx"),

  layout("routes/home.tsx", [
    index("routes/garment-list.tsx"),
    route("garment/new", "routes/garment-new.tsx"),
    route("garment/:id", "routes/garment-detail.tsx"),
    route("garment/:id/edit", "routes/garment-edit.tsx"),

    route("orders", "routes/order-list.tsx"),
    route("orders/:id", "routes/order-detail.tsx"),
    route("orders/:id/edit", "routes/order-edit.tsx"),
    route("cart", "routes/cart.tsx"),

    route("users", "routes/user-list.tsx"),
    route("users/:id", "routes/user-detail.tsx"),
  ]),

  route("*", "routes/not-found.tsx"),
] satisfies RouteConfig;
