function RegisterOutletForm() {
  return (
    <form className="outlet-form">
      <h2>Register Outlet</h2>

      <input type="date" placeholder="Valid From" />
      <input type="date" placeholder="Valid To" />
      <input type="text" placeholder="Outlet Name" />
      <input type="text" placeholder="Owner Name" />
      <input type="text" placeholder="Outlet Address" />
      <input type="text" placeholder="City" />
      <input type="text" placeholder="State" />
      <input type="text" placeholder="Mobile 1" />
      <input type="text" placeholder="Mobile 2" />
      <input type="email" placeholder="Email" />

      <button type="submit">Submit</button>
    </form>
  );
}

export default RegisterOutletForm;
