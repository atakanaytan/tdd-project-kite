import React from "react";
import { render } from "@testing-library/react";
import KiteFeed from "./KiteFeed";
import * as apiCalls from "../api/apiCalls";

const setup = (props) => {
  return render(<KiteFeed {...props} />);
};

const mockEmptyResponse = {
  data: {
    content: [],
  },
};

describe("KiteFeed", () => {
  describe("Lifecycle", () => {
    it("calls loadKites when it is rendered", () => {
      apiCalls.loadKites = jest.fn().mockResolvedValue(mockEmptyResponse);
      setup();
      expect(apiCalls.loadKites).toHaveBeenCalled();
    });
    it("calls loadKites with user parameter when it is rendered with user property", () => {
      apiCalls.loadKites = jest.fn().mockResolvedValue(mockEmptyResponse);
      setup({ user: "user1" });
      expect(apiCalls.loadKites).toHaveBeenCalledWith("user1");
    });
    it("calls loadKites without user parameter when it is rendered without user property", () => {
      apiCalls.loadKites = jest.fn().mockResolvedValue(mockEmptyResponse);
      setup();
      const parameter = apiCalls.loadKites.mock.calls[0][0];
      expect(parameter).toBeUndefined();
    });
  });
  describe("Layout", () => {
    it("displays no kite message when the response has empty page", () => {
      apiCalls.loadKites = jest.fn().mockResolvedValue(mockEmptyResponse);
      const { queryByText } = setup();
      expect(queryByText("There are no kites")).toBeInTheDocument();
    });
  });
});
