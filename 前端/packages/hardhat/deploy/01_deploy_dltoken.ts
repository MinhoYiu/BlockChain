import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

/**
 * Deploys a contract named "DLToken" using the deployer account.
 *
 * @param hre HardhatRuntimeEnvironment object.
 */
const deployDLToken: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  /*
    On localhost, the deployer account is the one that comes with Hardhat, which is already funded.

    When deploying to live networks (e.g `yarn deploy --network sepolia`), the deployer account
    should have sufficient balance to pay for the gas fees for contract creation.
  */
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  await deploy("DLToken", {
    from: deployer,
    // Contract constructor arguments
    // 注意：这里假设 DLToken 的构造函数没有参数。
    // 如果你的 DLToken.sol constructor 需要参数 (e.g. constructor(string memory name, string memory symbol))
    // 你需要在这里填充它们，例如: args: ["My DogeCoin", "MDG"],
    args: [],
    log: true,
    // autoMine: can be passed to the deploy function to make the deployment process faster on local networks by
    // automatically mining the contract deployment transaction. There is no effect on live networks.
    autoMine: true,
  });

  // Get the deployed contract to interact with it after deploying.
  const dlToken = await hre.ethers.getContract<Contract>("DLToken", deployer);
  console.log("DLToken deployed to:", await dlToken.getAddress());
};

export default deployDLToken;

// Tags are useful if you have multiple deploy files and only want to run one of them.
// e.g. yarn deploy --tags DLToken
deployDLToken.tags = ["DLToken"];