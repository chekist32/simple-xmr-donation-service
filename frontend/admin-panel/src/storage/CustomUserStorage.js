export function setUserStorage(userData) {
  localStorage.setItem("userData", JSON.stringify(userData));
}

export function getUserStorage() {
  return JSON.parse(localStorage.getItem("userData"));
}
