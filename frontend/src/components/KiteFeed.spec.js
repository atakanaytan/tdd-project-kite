import React from "react";
import { render, waitFor, screen } from "@testing-library/react";
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

const mockSuccessGetKitesSinglePage = {
  data: {
    content: [
      {
        id: 10,
        content: "This is the latest kite",
        date: 1561294668539,
        user: {
          id: 1,
          username: "user1",
          displayName: "display1",
          image: "profile.png",
        },
      },
    ],
    number: 0,
    first: true,
    last: true,
    size: 5,
    totalPages: 1,
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
    it("displays no kite message when the response has empty page", async () => {
      apiCalls.loadKites = jest.fn().mockResolvedValue(mockEmptyResponse);
      await (() =>
        expect(screen.getByText("There are no kites")).toBeInTheDocument());
    });
    it("does not display no kite message when the response has page of kite", async () => {
      apiCalls.loadKites = jest
        .fn()
        .mockResolvedValue(mockSuccessGetKitesSinglePage);
      const { queryByText } = setup();
      await waitFor(() =>
        expect(queryByText("There are no kites")).not.toBeInTheDocument()
      );
    });
    it("displays spinner when loading the kites", async () => {
      apiCalls.loadKites = jest.fn().mockImplementation(() => {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            resolve(mockSuccessGetKitesSinglePage);
          }, 300);
        });
      });
      const { queryByText } = setup();
      await waitFor(() =>
        expect(queryByText("There are no kites")).not.toBeInTheDocument()
      );
    });
    it("displays kite content", async () => {
      apiCalls.loadKites = jest
        .fn()
        .mockResolvedValue(mockSuccessGetKitesSinglePage);
      const { queryByText } = setup();
      await waitFor(() => {
        expect(queryByText("This is the latest kite")).toBeInTheDocument();
      });
    });
  });
});
